package com.ironhack.MemeBank.controller.impl;

import com.ironhack.MemeBank.dao.Money;
import com.ironhack.MemeBank.dao.Transaction;
import com.ironhack.MemeBank.dao.accounts.Account;
import com.ironhack.MemeBank.dao.users.ThirdParty;
import com.ironhack.MemeBank.dto.CreateAccountDTO;
import com.ironhack.MemeBank.dto.TransactionDTO;
import com.ironhack.MemeBank.enums.TransactionStatus;
import com.ironhack.MemeBank.enums.TransactionType;
import com.ironhack.MemeBank.repository.*;
import com.ironhack.MemeBank.service.impl.AccountService;
import com.ironhack.MemeBank.service.impl.TransactionService;
import com.ironhack.MemeBank.service.impl.UserServiceImpl;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.ironhack.MemeBank.enums.TransactionType.PENALTY_FEE;
import static org.springframework.data.util.Optionals.ifPresentOrElse;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
public class TransactionController {


    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    TransactionService transactionService;

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ThirdPartyRepository thirdPartyRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountHolderRepository accountHolderRepository;


    @GetMapping("/transactions")
    @ResponseStatus(HttpStatus.OK)
    public List<Transaction> getUserTransactions() {
        return transactionRepository.findByTransactionInitiator(userRepository.findByUsername(userServiceImpl.getCurrentUsername()).get());
    }

    @GetMapping("/transactions/all")
    @ResponseStatus(HttpStatus.OK)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @PostMapping("/transactions/third_party")
    public ResponseEntity<?> storeThirdPartyTransaction(@RequestBody TransactionDTO passedObject , @RequestHeader("hashed-key") String hashedKey
    ) {
//        if (GenericValidator.isBlankOrNull(hashedKey)) {
//            if(thirdPartyRepository.findByUsername(userServiceImpl.getCurrentUsername()).isPresent()) {
//                ThirdParty currentAuthor=thirdPartyRepository.findByUsername(userServiceImpl.getCurrentUsername()).get();
//                return new ResponseEntity<>("Header request must contain a valid HashedKey"+ Arrays.toString(currentAuthor.getHashKey()),
//                        HttpStatus.NOT_ACCEPTABLE);
//            }
//        }
        passedObject.setHashKey(hashedKey);
        passedObject.setTransactionInitiatorUserId(String.valueOf(thirdPartyRepository.findByUsername(userServiceImpl.getCurrentUsername()).get().getId()));
        Account testAccount=transactionService.evaluateAccounts(passedObject);


        if (GenericValidator.isBlankOrNull(String.valueOf(passedObject.getAmount())) || !GenericValidator.isDouble(passedObject.getAmount())) {
            return new ResponseEntity<>("Amount must be provided as a valid double",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (GenericValidator.isBlankOrNull(String.valueOf(passedObject.getAccountId())) || !GenericValidator.isLong(String.valueOf(passedObject.getAccountId()))) {
            return new ResponseEntity<>("Account ID must be provided as a valid long",
                    HttpStatus.NOT_ACCEPTABLE);
        } else if (accountRepository.findById(Long.valueOf(String.valueOf(passedObject.getAccountId()))).isEmpty()) {
            return new ResponseEntity<>("Account ID not found",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (GenericValidator.isBlankOrNull(String.valueOf(passedObject.getSecretKey()))) {
            return new ResponseEntity<>("Account secret key must be provided",
                    HttpStatus.NOT_ACCEPTABLE);
        }
        //apply monthly maintenance fee before processing of transaction
        transactionService.addMaintenanceFee(accountRepository.findById(Long.valueOf(passedObject.getAccountId())).get());

        Transaction newTransaction = new Transaction();
        newTransaction.setAccount(accountRepository.findById(Long.valueOf(passedObject.getAccountId())).get());
        Money balance           = accountService.checkAccountBalance(newTransaction.getAccount());
        Money transactionVolume = new Money(new BigDecimal(passedObject.getAmount()));
        newTransaction.setDescription(passedObject.getDescription());
        newTransaction.setDate(LocalDateTime.now());
        newTransaction.setAmount(new Money(new BigDecimal(passedObject.getAmount())));
//        newTransaction.setAvailableBalance(balance.getAmount());

        newTransaction.setTransactionInitiator(userRepository.findByUsername(userServiceImpl.getCurrentUsername()).get());


        if (transactionVolume.getAmount().compareTo(new BigDecimal(0)) < 0) {
            newTransaction.setType(TransactionType.CHARGE);
        } else {
            newTransaction.setType(TransactionType.TRANSFER);
        }

        BigDecimal newBalance     = new BigDecimal(String.valueOf(balance.getAmount().add(transactionVolume.getAmount())));
        Money      minimumBalance = accountService.findMinimumBalance(newTransaction.getAccount());
        Money       penaltyFeeVol = new Money(transactionService.newPenaltyFee(newTransaction.getAccount()).getAmount().getAmount());

        if (newBalance.compareTo(new BigDecimal(0)) < 0 && newTransaction.getType().equals(TransactionType.CHARGE)) {
            newTransaction.setStatus(TransactionStatus.DENIED);
            newTransaction.setResponseStatus(String.valueOf(new ResponseEntity<>("Transaction volume exceeded account balance. Transaction was denied", HttpStatus.NOT_ACCEPTABLE)));
            transactionRepository.save(newTransaction);
            return new ResponseEntity<>("Transaction volume exceeded account balance. Transaction was denied",
                    HttpStatus.NOT_ACCEPTABLE);
        } else
            //(newBalance.compareTo(new BigDecimal(0)) <= 0 && newTransaction.getType().equals(TransactionType.CHARGE))
        {
            newTransaction.setStatus(TransactionStatus.ACCEPTED);
            newTransaction.getAccount().setBalance(new Money(balance.increaseAmount(transactionVolume.getAmount())));
            newTransaction.setResponseStatus(String.valueOf(new ResponseEntity<>("Transaction was successful", HttpStatus.OK)));
            newTransaction.setAccount(accountRepository.save(newTransaction.getAccount()));
            transactionRepository.save(newTransaction);

            //apply penalty fee
            if (newBalance.compareTo(minimumBalance.getAmount()) < 0 && !accountService.checkLastpenaltyFee(newTransaction.getAccount(), newTransaction)) {
                Transaction newPenaltyFee = transactionService.newPenaltyFee(newTransaction.getAccount());

                newPenaltyFee.getAccount().setBalance(new Money(balance.increaseAmount(penaltyFeeVol)));
                newPenaltyFee.setResponseStatus(String.valueOf(new ResponseEntity<>("Transaction was successful", HttpStatus.OK)));
                newPenaltyFee.setAccount(accountRepository.save(newPenaltyFee.getAccount()));
                transactionRepository.save(newPenaltyFee);
            }

            return new ResponseEntity<>("Transaction was proceeded successfully",
                    HttpStatus.OK);
        }
    }


    @PostMapping("/transactions/{id}")
    public ResponseEntity<?> store(@RequestBody TransactionDTO passedObject, @PathVariable(name="id") String accountId) {

        if (GenericValidator.isBlankOrNull(accountId) || !GenericValidator.isLong(accountId)){
            return new ResponseEntity<>("Your Account ID must be provided as a valid long",
                    HttpStatus.NOT_ACCEPTABLE);
        } else if (accountRepository.findById(Long.valueOf(accountId)).isEmpty()
                || !userRepository.findByUsername(userServiceImpl.getCurrentUsername()).get().getPrimaryOwnedAccounts().contains(accountRepository.findById(Long.valueOf(accountId)).get())) {
            return new ResponseEntity<>("You cannot transfer funds from this account",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (GenericValidator.isBlankOrNull(String.valueOf(passedObject.getAmount())) || !GenericValidator.isDouble(passedObject.getAmount())) {
            return new ResponseEntity<>("Amount must be provided as a valid double",
                    HttpStatus.NOT_ACCEPTABLE);
        }else if(new BigDecimal(passedObject.getAmount()).compareTo(new BigDecimal(0))<=0){
            return new ResponseEntity<>("Amount for transfer must be a positive number",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (GenericValidator.isBlankOrNull(String.valueOf(passedObject.getAccountId())) || !GenericValidator.isLong(String.valueOf(passedObject.getAccountId()))) {
            return new ResponseEntity<>("Target Account ID must be provided as a valid long",
                    HttpStatus.NOT_ACCEPTABLE);
        } else if (accountRepository.findById(Long.valueOf(String.valueOf(passedObject.getAccountId()))).isEmpty()) {
            return new ResponseEntity<>("Account ID not found",
                    HttpStatus.NOT_ACCEPTABLE);
        }


        if (GenericValidator.isBlankOrNull(String.valueOf(passedObject.getOwnerName())) || userRepository.findByUsername(passedObject.getOwnerName()).isEmpty()) {
            return new ResponseEntity<>("Target Account owner name must be provided and valid",
                    HttpStatus.NOT_ACCEPTABLE);
//        } else if (!accountRepository.findByPrimaryOwnerOrSecondaryOwner(userRepository.findByUsername(passedObject.getOwnerName()).get(), userRepository.findByUsername(passedObject.getOwnerName()).get()).contains(accountRepository.findById(Long.valueOf(passedObject.getAccountId())).get())) {
        } else if (accountRepository.findByPrimaryOwnerOrSecondaryOwner(userRepository.findByUsername(passedObject.getOwnerName()).get(), userRepository.findByUsername(passedObject.getOwnerName()).get()).contains(passedObject.getAccountId().isEmpty())) {
            return new ResponseEntity<>("Account ID and account owner name does not match",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        transactionService.addMaintenanceFee(accountRepository.findById(Long.valueOf(accountId)).get());

        Transaction newTransaction = new Transaction();
        Account donorAccount=accountRepository.findById(Long.valueOf(accountId)).get();
        Account targetAccount=accountRepository.findById(Long.valueOf(passedObject.getAccountId())).get();
        newTransaction.setAccount(targetAccount);
        Money balance           = accountService.checkAccountBalance(donorAccount);
        Money transactionVolume = new Money(new BigDecimal(passedObject.getAmount()));
        newTransaction.setDescription(passedObject.getDescription());
        newTransaction.setDate(LocalDateTime.now());
        newTransaction.setAmount(new Money(new BigDecimal(passedObject.getAmount())));




        if(donorAccount.getBalance().getAmount().compareTo(transactionVolume.getAmount())<0){
            return new ResponseEntity<>("Insufficient funds on given account",
                    HttpStatus.NOT_ACCEPTABLE);
        }else{
            newTransaction.setStatus(TransactionStatus.ACCEPTED);
        }

        newTransaction.setTransactionInitiator(donorAccount.getPrimaryOwner());
        newTransaction.setTransactionInitiatorAccount(donorAccount);
        newTransaction.setType(TransactionType.TRANSFER);

        BigDecimal newBalance     = donorAccount.getBalance().getAmount().subtract(transactionVolume.getAmount());
        Money      minimumBalance = accountService.findMinimumBalance(donorAccount);

        donorAccount.setBalance(new Money(newBalance));
        accountRepository.save(donorAccount);

        targetAccount.setBalance(new Money(targetAccount.getBalance().increaseAmount(transactionVolume.getAmount())));
        newTransaction.setResponseStatus(String.valueOf(new ResponseEntity<>("Transaction was successful", HttpStatus.OK)));
        newTransaction.setAccount(accountRepository.save(targetAccount));
        transactionRepository.save(newTransaction);

            //apply penalty fee
            if (newBalance.compareTo(minimumBalance.getAmount()) < 0 && !accountService.checkLastpenaltyFee(donorAccount, newTransaction)) {
                Transaction newPenaltyFee = transactionService.newPenaltyFee(donorAccount);
                Money       penaltyFeeVol = new Money(transactionService.newPenaltyFee(donorAccount).getAmount().getAmount());
                newBalance = donorAccount.getBalance().getAmount().add(penaltyFeeVol.getAmount());
                newPenaltyFee.getAccount().setBalance(new Money(newBalance));
                newPenaltyFee.setResponseStatus(String.valueOf(new ResponseEntity<>("Transaction was successful", HttpStatus.OK)));
                newPenaltyFee.setAccount(accountRepository.save(newPenaltyFee.getAccount()));
                transactionRepository.save(newPenaltyFee);
            }
            return new ResponseEntity<>("Transaction was proceeded successfully",
                    HttpStatus.OK);
        }
    }




package com.ironhack.MemeBank.controller.impl;

import com.ironhack.MemeBank.dao.Money;
import com.ironhack.MemeBank.dao.Transaction;
import com.ironhack.MemeBank.dao.accounts.Account;
import com.ironhack.MemeBank.dto.CreateAccountDTO;
import com.ironhack.MemeBank.dto.TransactionDTO;
import com.ironhack.MemeBank.enums.TransactionStatus;
import com.ironhack.MemeBank.enums.TransactionType;
import com.ironhack.MemeBank.repository.AccountRepository;
import com.ironhack.MemeBank.repository.ThirdPartyRepository;
import com.ironhack.MemeBank.repository.TransactionRepository;
import com.ironhack.MemeBank.service.impl.AccountService;
import com.ironhack.MemeBank.service.impl.TransactionService;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.ironhack.MemeBank.enums.TransactionType.PENALTY_FEE;
import static org.springframework.data.util.Optionals.ifPresentOrElse;

import javax.validation.Valid;
import java.math.BigDecimal;
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
    ThirdPartyRepository thirdPartyRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @GetMapping("/third_party/transactions")
    @ResponseStatus(HttpStatus.OK)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @PostMapping("/third_party/transactions")
    public ResponseEntity<?> store(@RequestBody TransactionDTO passedObject //, @RequestHeader("hashed-key") String hashedKey
    ) {
        ResponseStatus responseStatus;

//        if (GenericValidator.isBlankOrNull(hashedKey) || thirdPartyRepository.findByHashKey(hashedKey).isEmpty()) {
//            return new ResponseEntity<>("Header request must contain a valid HashedKey",
//                    HttpStatus.NOT_ACCEPTABLE);
//        }


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

//        if (!Arrays.equals(accountRepository.findById(Long.valueOf(passedObject.getAccountId().get())).get().getSecretKey(), passedObject.getSecretKey().get().getBytes())) {
//            return new ResponseEntity<>("Provided secret key is invalid",
//                    HttpStatus.NOT_ACCEPTABLE);
//        }
        Transaction newTransaction = new Transaction();
        newTransaction.setAccount(accountRepository.findById(Long.valueOf(passedObject.getAccountId())).get());
        Money balance           = accountService.checkAccountBalance(newTransaction.getAccount());
        Money transactionVolume = new Money(new BigDecimal(passedObject.getAmount()));
        newTransaction.setDescription(passedObject.getDescription());
        newTransaction.setDate(LocalDateTime.now());
        newTransaction.setAmount(new Money(new BigDecimal(passedObject.getAmount())));
        newTransaction.setAvailableBalance(balance.getAmount());
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
//        else {
//            newTransaction.setStatus(TransactionStatus.ACCEPTED);
//            newTransaction.getAccount().setBalance(new Money(balance.increaseAmount((transactionVolume.getAmount()))));
//            newTransaction.setResponseStatus(String.valueOf(new ResponseEntity<String>("Transaction was successful", HttpStatus.OK)));
//            newTransaction.setAccount(accountRepository.save(newTransaction.getAccount()));
//            transactionRepository.save(newTransaction);
//            //apply penalty fee
//            if(newBalance.compareTo(minimumBalance.getAmount())<0 && !accountService.checkLastpenaltyFee(newTransaction.getAccount(),newTransaction)){
//                transactionRepository.save(transactionService.newPenaltyFee(newTransaction.getAccount()));
//            }
//            return new ResponseEntity<>("Transaction was proceeded successfully",
//                    HttpStatus.OK);
//        }


    }
    }



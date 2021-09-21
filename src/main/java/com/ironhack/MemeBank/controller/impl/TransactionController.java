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
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@RestController
public class TransactionController {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    ThirdPartyRepository thirdPartyRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @PostMapping("/third_party/transaction")
    public ResponseEntity<?> store(@RequestBody @Valid TransactionDTO passedObject, @RequestHeader("hashed-key") String hashedKey) {
        ResponseStatus responseStatus;

        if (GenericValidator.isBlankOrNull(hashedKey) || thirdPartyRepository.findByHashKey(hashedKey).isEmpty()) {
            return new ResponseEntity<>("Header request must contain a valid HashedKey",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (GenericValidator.isBlankOrNull(passedObject.getAmount().get()) || !GenericValidator.isDouble(passedObject.getAmount().get())) {
            return new ResponseEntity<>("Amount must be provided as a valid double",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (GenericValidator.isBlankOrNull(passedObject.getAccountId().get()) || !GenericValidator.isLong(passedObject.getAccountId().get())) {
            return new ResponseEntity<>("Account ID must be provided as a valid long",
                    HttpStatus.NOT_ACCEPTABLE);
        } else if (accountRepository.findById(Long.valueOf(passedObject.getAccountId().get())).isEmpty()) {
            return new ResponseEntity<>("Account ID not found",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (GenericValidator.isBlankOrNull(passedObject.getSecretKey().get())) {
            return new ResponseEntity<>("Account secret key must be provided",
                    HttpStatus.NOT_ACCEPTABLE);
        }
        if (!Arrays.equals(accountRepository.findById(Long.valueOf(passedObject.getAccountId().get())).get().getSecretKey(), passedObject.getSecretKey().get().getBytes())) {
            return new ResponseEntity<>("Provided secret key is invalid",
                    HttpStatus.NOT_ACCEPTABLE);
        }
        Transaction newTransaction    = new Transaction();
        Money       balance           = accountService.checkAccountBalance(accountRepository.findById(Long.valueOf(passedObject.getAccountId().get())).get());
        Money       transactionVolume = new Money(new BigDecimal(passedObject.getAmount().get()));
        newTransaction.setDescription(passedObject.getDescription().get());
        newTransaction.setDate(LocalDateTime.now());
        newTransaction.setAccount(accountRepository.findById(Long.valueOf(passedObject.getAccountId().get())).get());
        newTransaction.setAmount(new Money(new BigDecimal(passedObject.getAmount().get())));
        newTransaction.setAvailableBalance(balance.getAmount());
        if (transactionVolume.getAmount().compareTo(new BigDecimal(0)) < 0) {
            newTransaction.setType(TransactionType.CHARGE);
        } else {
            newTransaction.setType(TransactionType.TRANSFER);
        }


        if (transactionVolume.getAmount().compareTo(new BigDecimal(0)) < 0) {
            newTransaction.setStatus(TransactionStatus.DENIED);
            newTransaction.setResponseStatus(String.valueOf((ResponseStatus) new ResponseEntity<String>("Transaction volume exceeded account balance. Transaction was denied", HttpStatus.NOT_ACCEPTABLE)));
            responseStatus = (ResponseStatus) new ResponseEntity<String>("Transaction volume exceeded account balance. Transaction was denied",
                    HttpStatus.NOT_ACCEPTABLE);
        }else {
            newTransaction.setStatus(TransactionStatus.ACCEPTED);
            newTransaction.setResponseStatus(String.valueOf((ResponseStatus) new ResponseEntity<String>("Transaction was successful", HttpStatus.OK)));
            responseStatus = (ResponseStatus) new ResponseEntity<String>("Transaction was proceeded successfully",
                    HttpStatus.OK);
        }
        transactionRepository.save(newTransaction);
        return (ResponseEntity<?>) responseStatus;
    }
}


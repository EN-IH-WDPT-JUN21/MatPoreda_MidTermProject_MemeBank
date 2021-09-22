package com.ironhack.MemeBank.service.impl;

import com.ironhack.MemeBank.dao.Money;
import com.ironhack.MemeBank.dao.Transaction;
import com.ironhack.MemeBank.dao.accounts.Account;
import com.ironhack.MemeBank.dto.CreateAccountDTO;
import com.ironhack.MemeBank.dto.TransactionDTO;
import com.ironhack.MemeBank.enums.AccountType;
import com.ironhack.MemeBank.enums.TransactionStatus;
import com.ironhack.MemeBank.enums.TransactionType;
import com.ironhack.MemeBank.repository.AccountRepository;
import com.ironhack.MemeBank.repository.TransactionRepository;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
@Service
public class TransactionService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;

    public Transaction newPenaltyFee(Account account){
        Transaction newTransaction    = new Transaction();
        newTransaction.setAccount(account);
        newTransaction.setType(TransactionType.PENALTY_FEE);
        newTransaction.setAvailableBalance(account.getBalance().getAmount());
        newTransaction.setStatus(TransactionStatus.ACCEPTED);
        newTransaction.setAmount(new Money(new BigDecimal(0).subtract(account.getPenaltyFee().getAmount())));
        newTransaction.setDate(LocalDateTime.now());
        newTransaction.setDescription("Penalty for reaching the account limit");
        newTransaction.setResponseStatus(String.valueOf(new ResponseEntity<String>("PenaltyFee Applied",
                HttpStatus.CREATED)));
        return newTransaction;
    }

    public ResponseEntity<?> storeTransaction(@Valid TransactionDTO passedObject) {
        LocalDateTime     date;
        String description;
        TransactionType type;
        TransactionStatus status;
        Money amount;
        BigDecimal        availableBalance;
        Optional<Account> account;
        Transaction       newTransaction = new Transaction();

            if (GenericValidator.isBlankOrNull(passedObject.getDate().toString())) {
                newTransaction.setDate(LocalDateTime.now());
            } else {
                if (GenericValidator.isDate(passedObject.getDate().toString(), "dd-MM-yyyy hh:mm:ss", true)) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss");
                        formatter = formatter.withLocale( Locale.ENGLISH );
                        date = LocalDateTime.parse(passedObject.getDate().toString(), formatter);
                        newTransaction.setDate(date);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseEntity<>("Invalid transaction date format. Please please provide date in dd-MM-yyyy hh:mm:ss",
                                HttpStatus.NOT_ACCEPTABLE);
                    }
                } else {
                    return new ResponseEntity<>("Invalid transaction date format. Please please provide date in dd-MM-yyyy hh:mm:ss",
                            HttpStatus.NOT_ACCEPTABLE);
                }
            }

        if (GenericValidator.isBlankOrNull(passedObject.getDescription())) {
            newTransaction.setDescription(passedObject.getDescription());
        } else {
            newTransaction.setDescription(null);
        }

        if (GenericValidator.isBlankOrNull(passedObject.getType().toString())) {
            return new ResponseEntity<>("Transaction type must be provided",
                    HttpStatus.NOT_ACCEPTABLE);
        }
        String transactionType =passedObject.getType().toUpperCase().replaceAll("\\s+","");
        boolean validTransaction=false;
        for (TransactionType t : TransactionType.values())
        { if (transactionType.equalsIgnoreCase(t.toString())){ validTransaction=true;}
        }
        if(!validTransaction) {
            return new ResponseEntity<>("Transaction of type: ".concat(transactionType.toString()).concat(" does not exist."),
                    HttpStatus.NOT_ACCEPTABLE);
        }
        type=TransactionType.valueOf(transactionType);

        if(GenericValidator.isBlankOrNull(passedObject.getAccountId())){
            return new ResponseEntity<>("Account ID must be provided",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if(GenericValidator.isBlankOrNull(passedObject.getAmount()) || GenericValidator.isDouble(passedObject.getAmount())){
            return new ResponseEntity<>("Amount must be provided and must be numeric",
                    HttpStatus.NOT_ACCEPTABLE);
        }else{
            amount=new Money(new BigDecimal(passedObject.getAmount()));
        }

        if(GenericValidator.isBlankOrNull(passedObject.getAccountId()) || !GenericValidator.isLong(passedObject.getAccountId())){
            return new ResponseEntity<>("Account Id must be provided and must be numeric",
                    HttpStatus.NOT_ACCEPTABLE);
        }else{
            account=accountRepository.findById(Long.valueOf(passedObject.getAccountId()));
        }

        if(account.isEmpty()){
            return new ResponseEntity<>("Account with given Id does not exist",
                    HttpStatus.NOT_ACCEPTABLE);
        }else{
            availableBalance=account.get().getBalance().getAmount();
        }




//            private TransactionStatus status;

        return new ResponseEntity<>("Transaction has ben created",
                HttpStatus.CREATED);
    }
}

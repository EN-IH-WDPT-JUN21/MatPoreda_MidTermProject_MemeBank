package com.ironhack.MemeBank.service.impl;

import com.ironhack.MemeBank.dao.Money;
import com.ironhack.MemeBank.dao.Transaction;
import com.ironhack.MemeBank.dao.accounts.Account;
import com.ironhack.MemeBank.dao.users.ThirdParty;
import com.ironhack.MemeBank.dao.users.User;
import com.ironhack.MemeBank.dto.TransactionDTO;
import com.ironhack.MemeBank.enums.AccountType;
import com.ironhack.MemeBank.enums.TransactionStatus;
import com.ironhack.MemeBank.enums.TransactionType;
import com.ironhack.MemeBank.repository.*;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SavingsRepository savingsRepository;

    @Autowired
    CreditCardRepository creditCardRepository;

    @Autowired
    ThirdPartyRepository thirdPartyRepository;

    @Autowired
    TransactionRepository transactionRepository;

    public Transaction newPenaltyFee(Account account){
        Transaction newTransaction    = new Transaction();
        newTransaction.setAccount(account);
        newTransaction.setType(TransactionType.PENALTY_FEE);
//        newTransaction.setAvailableBalance(account.getBalance().getAmount());
        newTransaction.setStatus(TransactionStatus.ACCEPTED);
        newTransaction.setAmount(new Money(new BigDecimal(0).subtract(account.getPenaltyFee().getAmount())));
        newTransaction.setDate(LocalDateTime.now());
        newTransaction.setDescription("Penalty for reaching the account limit");
        newTransaction.setResponseStatus(String.valueOf(new ResponseEntity<>("PenaltyFee Applied",
                HttpStatus.CREATED)));
        newTransaction.setTransactionInitiator(null);
        newTransaction.setTransactionInitiatorAccount(null);
        return newTransaction;
    }

    public void addMaintenanceFee(Account account) {
        AccountType accountType = account.getAccountType();
        if (accountType.equals(AccountType.CHECKING)) {

            Optional<Transaction> lastMaintenanceFee = transactionRepository.findLastMonthlyMaintenanceFee(account.getId());
            LocalDate             calculationDate    = lastMaintenanceFee.isPresent() ? lastMaintenanceFee.get().getDate().toLocalDate() : account.getCreationDate();

            long monthsBetween = ChronoUnit.MONTHS.between(
                    YearMonth.from(calculationDate),
                    YearMonth.from(LocalDateTime.now())
            );
            if (Math.floor((int) monthsBetween) >= 1
                    //check if last fee was in last month
                    && ChronoUnit.DAYS.between(calculationDate, LocalDate.now())>=LocalDate.now().lengthOfMonth()
            ) {
                int numberOfTransactions = (int) Math.floor((int) monthsBetween);
                for (int i = numberOfTransactions; i > 0; i--) {
                    Transaction newTransaction = new Transaction();
                    newTransaction.setAccount(account);
                    newTransaction.setType(TransactionType.MONTHLY_MAINTENANCE_FEE);
                    newTransaction.setStatus(TransactionStatus.ACCEPTED);
                    newTransaction.setAmount(new Money(new BigDecimal(0).subtract(account.getMonthlyMaintenanceFee().getAmount())));

                    LocalDate initialDate     = LocalDate.now().minusMonths(i);
                    LocalDate transactionDate = initialDate.withDayOfMonth(initialDate.lengthOfMonth());
                    newTransaction.setDate(transactionDate.atStartOfDay());
                    newTransaction.setDescription("Monthly maintenance fee");
                    newTransaction.setResponseStatus(String.valueOf(new ResponseEntity<>("Monthly maintenance fee applied",
                            HttpStatus.CREATED)));
                    newTransaction.setTransactionInitiator(null);
                    newTransaction.setTransactionInitiatorAccount(null);
                    account.setBalance(new Money(account.getBalance().getAmount().subtract(account.getMonthlyMaintenanceFee().getAmount())));
                    accountRepository.save(account);
                    transactionRepository.save(newTransaction);
                }
            }
        }

    }


    public void addInterestRates(Account account) {
        AccountType accountType = account.getAccountType();
        if (accountType.equals(AccountType.SAVINGS) || accountType.equals(AccountType.CREDIT_CARD)) {

            Optional<Transaction> lastInterestsAccrual = transactionRepository.findLastInterestRatesAccrual(account.getId());
            LocalDate             calculationDate      = lastInterestsAccrual.isPresent() ? lastInterestsAccrual.get().getDate().toLocalDate() : account.getCreationDate();

            long monthsBetween = ChronoUnit.MONTHS.between(
                    YearMonth.from(calculationDate),
                    YearMonth.from(LocalDateTime.now())
            );
            switch (accountType) {
                case SAVINGS: {
                    if (Math.floor((int) monthsBetween) >= 12
                            //check if last fee was in last month
                            && ChronoUnit.DAYS.between(calculationDate, LocalDate.now()) >= LocalDate.now().lengthOfYear()
                    ) {
                        int numberOfTransactions = (int) Math.floor(monthsBetween/12L);
                        for (int i = numberOfTransactions; i > 0; i--) {
                            Transaction newTransaction = new Transaction();
                            newTransaction.setAccount(account);
                            newTransaction.setType(TransactionType.ACCRUAL);
                            newTransaction.setStatus(TransactionStatus.ACCEPTED);
                            BigDecimal interestRate=savingsRepository.findById(account.getId()).get().getInterestRate();
                            newTransaction.setAmount(new Money(account.getBalance().getAmount().multiply(interestRate)));

                            LocalDate initialDate     = LocalDate.now().minusMonths(i* 12L);
                            LocalDate transactionDate = initialDate.withDayOfMonth(initialDate.lengthOfMonth());
                            newTransaction.setDate(transactionDate.atStartOfDay());
                            newTransaction.setDescription("Interest rates accrual");
                            newTransaction.setResponseStatus(String.valueOf(new ResponseEntity<>("Interest rates accrual applied",
                                    HttpStatus.CREATED)));
                            newTransaction.setTransactionInitiator(null);
                            newTransaction.setTransactionInitiatorAccount(null);
                            account.setBalance(new Money(account.getBalance().getAmount().add(newTransaction.getAmount().getAmount())));
                            accountRepository.save(account);
                            transactionRepository.save(newTransaction);
                        }
                    }
                }

                case CREDIT_CARD: {
                    if (Math.floor((int) monthsBetween) >= 1
                            //check if last fee was in last month
                            && ChronoUnit.DAYS.between(calculationDate, LocalDate.now()) >= LocalDate.now().lengthOfMonth()
                    ) {
                        int numberOfTransactions = (int) Math.floor(monthsBetween);
                        for (int i = numberOfTransactions; i > 0; i--) {
                            Transaction newTransaction = new Transaction();
                            newTransaction.setAccount(account);
                            newTransaction.setType(TransactionType.ACCRUAL);
                            newTransaction.setStatus(TransactionStatus.ACCEPTED);
                            BigDecimal interestRate=creditCardRepository.findById(account.getId()).get().getInterestRate();
                            BigDecimal creditLimit=creditCardRepository.findById(account.getId()).get().getCreditLimit().getAmount();
                            BigDecimal creditOwed=creditLimit.subtract(account.getBalance().getAmount());
                            newTransaction.setAmount(new Money(new BigDecimal(0).subtract(creditOwed.multiply(interestRate))));

                            LocalDate initialDate     = LocalDate.now().minusMonths(i);
                            LocalDate transactionDate = initialDate.withDayOfMonth(initialDate.lengthOfMonth());
                            newTransaction.setDate(transactionDate.atStartOfDay());
                            newTransaction.setDescription("Interest rates accrual");
                            newTransaction.setResponseStatus(String.valueOf(new ResponseEntity<>("Interest rates accrual applied",
                                    HttpStatus.CREATED)));
                            newTransaction.setTransactionInitiator(null);
                            newTransaction.setTransactionInitiatorAccount(null);
                            account.setBalance(new Money(account.getBalance().getAmount().add(newTransaction.getAmount().getAmount())));
                            accountRepository.save(account);
                            transactionRepository.save(newTransaction);
                        }
                    }
                }
            }
        }
    }

    public ResponseEntity<?> storeTransaction(@Valid TransactionDTO passedObject) {
        LocalDateTime     date;
        TransactionType type;
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

        if (GenericValidator.isBlankOrNull(passedObject.getType())) {
            return new ResponseEntity<>("Transaction type must be provided",
                    HttpStatus.NOT_ACCEPTABLE);
        }
        String transactionType =passedObject.getType().toUpperCase().replaceAll("\\s+","");
        boolean validTransaction=false;
        for (TransactionType t : TransactionType.values())
        { if (transactionType.equalsIgnoreCase(t.toString())){ validTransaction=true;}
        }
        if(!validTransaction) {
            return new ResponseEntity<>("Transaction of type: ".concat(transactionType).concat(" does not exist."),
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

        return new ResponseEntity<>("Transaction has ben created",
                HttpStatus.CREATED);
    }


    public Account evaluateAccounts(TransactionDTO transactionDTO) {
        Account account = null;
        if (accountRepository.findById(Long.valueOf(transactionDTO.getAccountId())).isPresent() && thirdPartyRepository.findById(Long.valueOf(transactionDTO.getTransactionInitiatorUserId())).isPresent()) {
            account = accountRepository.findById(Long.valueOf(transactionDTO.getAccountId())).get();
            ThirdParty thirdParty = thirdPartyRepository.findById(Long.valueOf(transactionDTO.getTransactionInitiatorUserId())).get();
            if (account.getSecretKey().equals(transactionDTO.getSecretKey()) && thirdParty.getHashKey().equals(transactionDTO.getHashKey())) {
                return account;
            } else if (!account.getSecretKey().equals(transactionDTO.getSecretKey())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong Secret Key. Account"+ account.getSecretKey()+"passed: "+transactionDTO.getSecretKey());
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Wrong Hashed key");
            }
        }
        return null;
    }

    public boolean checkIfTodaysTransactionVolumeIsGreaterThanMaxDailyVolume(Long userId, Transaction transaction){
        if(userRepository.findById(userId).isPresent()) {
            User       user             = userRepository.findById(userId).get();
            BigDecimal max24HVolume     = transactionRepository.findMaxDailyVolume(userId);
            BigDecimal current24HVolume = transactionRepository.findTransactionVolumeInLast24H(user.getId());
            if (max24HVolume.compareTo(new BigDecimal("0.00"))<=0) {
                return false;
            } else return max24HVolume.multiply(new BigDecimal("1.5")).compareTo(current24HVolume.add(transaction.getAmount().getAmount())) < 0;

        }
        return false;
    }


}

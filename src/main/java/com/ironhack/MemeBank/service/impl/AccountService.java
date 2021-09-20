package com.ironhack.MemeBank.service.impl;

import com.ironhack.MemeBank.dao.Money;
import com.ironhack.MemeBank.dao.accounts.Checking;
import com.ironhack.MemeBank.dao.accounts.CreditCard;
import com.ironhack.MemeBank.dao.accounts.Savings;
import com.ironhack.MemeBank.dao.accounts.StudentChecking;
import com.ironhack.MemeBank.dao.users.AccountHolder;
import com.ironhack.MemeBank.dto.CreateAccountDTO;
import com.ironhack.MemeBank.enums.AccountType;
import com.ironhack.MemeBank.enums.Status;
import com.ironhack.MemeBank.repository.*;
import com.ironhack.MemeBank.security.Passwords;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.crypto.spec.SecretKeySpec;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    ThirdPartyRepository thirdPartyRepository;

    @Autowired
    AccountHolderRepository accountHolderRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CheckingRepository checkingRepository;

    @Autowired
    StudentCheckingRepository studentCheckingRepository;

    @Autowired
    SavingsRepository savingsRepository;

    @Autowired
    CreditCardRepository creditCardRepository;

    public ResponseEntity<?> storeSavings(@Valid CreateAccountDTO passedObject) {

        //check if primary owner is provided
        if (GenericValidator.isBlankOrNull(passedObject.getPrimaryOwnerName())) {
            return new ResponseEntity<>("Primary owner must be specified.", HttpStatus.NOT_ACCEPTABLE);
        }
        Optional<AccountHolder> primaryOwner = accountHolderRepository.findByName(passedObject.getPrimaryOwnerName());
        if (primaryOwner.isEmpty()) {
            return new ResponseEntity<>("User with name ".concat(passedObject.getPrimaryOwnerName()).concat(" does not exist! Please create user first!"),
                    HttpStatus.NOT_ACCEPTABLE);
        }

        Savings newAccount = new Savings();
        if (passedObject.getBalance().isEmpty()) {
            newAccount.setBalance(new Money(new BigDecimal(100)));
        } else if (!GenericValidator.isDouble(passedObject.getBalance().get()) || Long.parseLong(passedObject.getBalance().get()) < Long.parseLong("100")) {
            return new ResponseEntity<>("Starting balance for savings account must be numeric and cannot be lower than minimum balance.",
                    HttpStatus.NOT_ACCEPTABLE);
        } else {
            try {
                newAccount.setBalance(new Money(new BigDecimal(String.valueOf(passedObject.getBalance().get()))));
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>("Minimum balance must be provided.",
                        HttpStatus.NOT_ACCEPTABLE);
            }

        }

                var salt     = Passwords.getNextSalt();
                var password = primaryOwner.get().getPassword().toCharArray();
                var secretKey=Passwords.hash(password, salt);
                newAccount.setSecretKey(secretKey);
                newAccount.setSalt(salt);

        newAccount.setPenaltyFee(new Money(BigDecimal.valueOf(40)));

        if (GenericValidator.isBlankOrNull(passedObject.getCreationDate().get())) {
            newAccount.setCreationDate(new Date());
        } else {
            if (GenericValidator.isDate(passedObject.getCreationDate().get(), "dd-MM-yyyy", true)) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                try {
                    newAccount.setCreationDate(formatter.parse(passedObject.getCreationDate().get()));
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ResponseEntity<>("Invalid creation date format. Please please provide date in dd-MM-yyyy format",
                            HttpStatus.NOT_ACCEPTABLE);
                }
            } else {
                return new ResponseEntity<>("Invalid creation date format. Please please provide date in dd-MM-yyyy format",
                        HttpStatus.NOT_ACCEPTABLE);
            }
        }

        newAccount.setStatus(Status.ACTIVE);

        newAccount.setPrimaryOwner(primaryOwner.get());
        if (passedObject.getSecondaryOwnerName() != null && passedObject.getSecondaryOwnerName().isPresent()) {

            Optional<AccountHolder> secondaryOwner = accountHolderRepository.findByName(passedObject.getSecondaryOwnerName().get());
            secondaryOwner.ifPresent(newAccount::setSecondaryOwner);
        }


        if (passedObject.getInterestRate() != null && passedObject.getInterestRate().isPresent()) {
            if (GenericValidator.isDouble(passedObject.getInterestRate().get())) {
                newAccount.setInterestRate(new BigDecimal(passedObject.getInterestRate().get()));
            } else {
                return new ResponseEntity<>("Invalid input for interest rate. Please provide numeric data",
                        HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            newAccount.setInterestRate(new BigDecimal(0.0025));
        }

        if (passedObject.getMinimumBalance() != null && passedObject.getMinimumBalance().isPresent()) {
            if (GenericValidator.isDouble(passedObject.getMinimumBalance().get())) {
                newAccount.setMinimumBalance(new Money(new BigDecimal(passedObject.getMinimumBalance().get())));
            } else {
                return new ResponseEntity<>("Invalid input for minimum balance. Please provide numeric data",
                        HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            newAccount.setMinimumBalance(new Money(new BigDecimal(1000)));
        }

        newAccount.setPenaltyFee(new Money(new BigDecimal(40)));

        savingsRepository.save(newAccount);

        return new ResponseEntity<>("New savings account created",
                HttpStatus.CREATED);
    }


    public ResponseEntity<?> storeChecking(@Valid CreateAccountDTO passedObject) {

        //check if primary owner is provided
        if (GenericValidator.isBlankOrNull(passedObject.getPrimaryOwnerName())) {
            return new ResponseEntity<>("Primary owner must be specified.", HttpStatus.NOT_ACCEPTABLE);
        }
        Optional<AccountHolder> primaryOwner = accountHolderRepository.findByName(passedObject.getPrimaryOwnerName());
        if (primaryOwner.isEmpty()) {
            return new ResponseEntity<>("User with name ".concat(passedObject.getPrimaryOwnerName()).concat(" does not exist! Please create user first!"),
                    HttpStatus.NOT_ACCEPTABLE);
        }


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
            formatter = formatter.withLocale(Locale.ENGLISH);
            LocalDate birthDate = LocalDate.parse(primaryOwner.get().getDateOfBirth(), formatter);
            LocalDate today     = LocalDate.now();
            int       years     = Period.between(birthDate, today).getYears();
            String accountType = (years < 24) ?  "STUDENT_CHECKING" : "CHECKING";


        switch (accountType) {

            case "CHECKING": {
                Checking newAccount = new Checking();
                if (passedObject.getBalance().isEmpty()) {
                    newAccount.setBalance(new Money(new BigDecimal(100)));
                } else if (!GenericValidator.isDouble(passedObject.getBalance().get()) || Long.parseLong(passedObject.getBalance().get()) < Long.parseLong("100")) {
                    return new ResponseEntity<>("Starting balance for checking account cannot be lower than minimum balance.",
                            HttpStatus.NOT_ACCEPTABLE);
                } else {
                    try {
                        newAccount.setBalance(new Money(new BigDecimal(String.valueOf(passedObject.getBalance().get()))));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseEntity<>("Balance must be provided as a valid number.",
                                HttpStatus.NOT_ACCEPTABLE);
                    }

                }

                var salt     = Passwords.getNextSalt();
                var password = primaryOwner.get().getPassword().toCharArray();
                var secretKey=Passwords.hash(password, salt);
                newAccount.setSecretKey(secretKey);
                newAccount.setSalt(salt);

                newAccount.setPenaltyFee(new Money(BigDecimal.valueOf(40)));

                if (GenericValidator.isBlankOrNull(passedObject.getCreationDate().get())) {
                    newAccount.setCreationDate(new Date());
                } else {
                    if (GenericValidator.isDate(passedObject.getCreationDate().get(), "dd-MM-yyyy", true)) {
                        try {
                            newAccount.setCreationDate((Date) formatter.parse(passedObject.getCreationDate().get()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new ResponseEntity<>("Invalid creation date format. Please please provide date in dd-MM-yyyy format",
                                    HttpStatus.NOT_ACCEPTABLE);
                        }
                    } else {
                        return new ResponseEntity<>("Invalid creation date format. Please please provide date in dd-MM-yyyy format",
                                HttpStatus.NOT_ACCEPTABLE);
                    }
                }

                newAccount.setStatus(Status.ACTIVE);

                newAccount.setPrimaryOwner(primaryOwner.get());
                if (passedObject.getSecondaryOwnerName() != null && passedObject.getSecondaryOwnerName().isPresent()) {

                    Optional<AccountHolder> secondaryOwner = accountHolderRepository.findByName(passedObject.getSecondaryOwnerName().get());
                    secondaryOwner.ifPresent(newAccount::setSecondaryOwner);
                }


                if (passedObject.getMonthlyMaintenanceFee() != null && passedObject.getMonthlyMaintenanceFee().isPresent()) {
                    if (GenericValidator.isDouble(passedObject.getMonthlyMaintenanceFee().get())) {
                        newAccount.setMonthlyMaintenanceFee(new Money(new BigDecimal(passedObject.getMonthlyMaintenanceFee().get())));
                    } else {
                        return new ResponseEntity<>("Invalid input for Monthly Maintenance Fee. Please provide numeric data",
                                HttpStatus.NOT_ACCEPTABLE);
                    }
                } else {
                    newAccount.setMonthlyMaintenanceFee(new Money(new BigDecimal(12)));
                }

                if (passedObject.getMinimumBalance() != null && passedObject.getMinimumBalance().isPresent()) {
                    if (GenericValidator.isDouble(passedObject.getMinimumBalance().get())) {
                        newAccount.setMinimumBalance(new Money(new BigDecimal(passedObject.getMinimumBalance().get())));
                    } else {
                        return new ResponseEntity<>("Invalid input for minimum balance. Please provide numeric data",
                                HttpStatus.NOT_ACCEPTABLE);
                    }
                } else {
                    newAccount.setMinimumBalance(new Money(new BigDecimal(250)));
                }

                newAccount.setPenaltyFee(new Money(new BigDecimal(40)));

                checkingRepository.save(newAccount);
                break;
            }


            case "STUDENT_CHECKING": {
                StudentChecking newAccount = new StudentChecking();
                if (passedObject.getBalance().isEmpty()) {
                    newAccount.setBalance(new Money(new BigDecimal(0)));
                } else if (!GenericValidator.isDouble(passedObject.getBalance().get()) || Long.parseLong(passedObject.getBalance().get()) < Long.parseLong("0")) {
                    return new ResponseEntity<>("Starting balance for student checking account cannot be lower than 0.",
                            HttpStatus.NOT_ACCEPTABLE);
                } else {
                    try {
                        newAccount.setBalance(new Money(new BigDecimal(String.valueOf(passedObject.getBalance().get()))));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseEntity<>("Balance must be provided as a valid number.",
                                HttpStatus.NOT_ACCEPTABLE);
                    }

                }

                var salt     = Passwords.getNextSalt();
                var password = primaryOwner.get().getPassword().toCharArray();
                var secretKey=Passwords.hash(password, salt);
                newAccount.setSecretKey(secretKey);
                newAccount.setSalt(salt);

                newAccount.setPenaltyFee(new Money(BigDecimal.valueOf(40)));

                if (GenericValidator.isBlankOrNull(passedObject.getCreationDate().get())) {
                    newAccount.setCreationDate(new Date());
                } else {
                    if (GenericValidator.isDate(passedObject.getCreationDate().get(), "dd-MM-yyyy", true)) {
                        try {
                            newAccount.setCreationDate((Date) formatter.parse(passedObject.getCreationDate().get()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new ResponseEntity<>("Invalid creation date format. Please please provide date in dd-MM-yyyy format",
                                    HttpStatus.NOT_ACCEPTABLE);
                        }
                    } else {
                        return new ResponseEntity<>("Invalid creation date format. Please please provide date in dd-MM-yyyy format",
                                HttpStatus.NOT_ACCEPTABLE);
                    }
                }

                newAccount.setStatus(Status.ACTIVE);

                newAccount.setPrimaryOwner(primaryOwner.get());
                if (passedObject.getSecondaryOwnerName() != null && passedObject.getSecondaryOwnerName().isPresent()) {

                    Optional<AccountHolder> secondaryOwner = accountHolderRepository.findByName(passedObject.getSecondaryOwnerName().get());
                    secondaryOwner.ifPresent(newAccount::setSecondaryOwner);
                }

                newAccount.setPenaltyFee(new Money(new BigDecimal(40)));

                studentCheckingRepository.save(newAccount);
                break;
            }
        }


        return new ResponseEntity<>("New ".concat(accountType).concat(" account created"),
                HttpStatus.CREATED);
    }

    public ResponseEntity<?> storeCreditCard(@Valid CreateAccountDTO passedObject) {
        Optional<AccountHolder> primaryOwner = accountHolderRepository.findByName(passedObject.getPrimaryOwnerName());

                CreditCard newAccount = new CreditCard();
                if (passedObject.getBalance().isEmpty()) {
                    newAccount.setBalance(new Money(new BigDecimal(0)));
                } else if (!GenericValidator.isDouble(passedObject.getBalance().get()) || Long.parseLong(passedObject.getBalance().get()) < Long.parseLong("100")) {
                    return new ResponseEntity<>("Starting balance for credit card account cannot be lower than credit.",
                            HttpStatus.NOT_ACCEPTABLE);
                } else {
                    try {
                        newAccount.setBalance(new Money(new BigDecimal(String.valueOf(passedObject.getBalance().get()))));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseEntity<>("Balance must be provided as a valid number.",
                                HttpStatus.NOT_ACCEPTABLE);
                    }

                }

                var salt     = Passwords.getNextSalt();
                var password = primaryOwner.get().getPassword().toCharArray();
                var secretKey=Passwords.hash(password, salt);
                newAccount.setSecretKey(secretKey);
                newAccount.setSalt(salt);

                newAccount.setPenaltyFee(new Money(BigDecimal.valueOf(40)));

                if (GenericValidator.isBlankOrNull(passedObject.getCreationDate().get())) {
                    newAccount.setCreationDate(new Date());
                } else {
                    if (GenericValidator.isDate(passedObject.getCreationDate().get(), "dd-MM-yyyy", true)) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                        try {
                            newAccount.setCreationDate(formatter.parse(passedObject.getCreationDate().get()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new ResponseEntity<>("Invalid creation date format. Please please provide date in dd-MM-yyyy format",
                                    HttpStatus.NOT_ACCEPTABLE);
                        }
                    } else {
                        return new ResponseEntity<>("Invalid creation date format. Please please provide date in dd-MM-yyyy format",
                                HttpStatus.NOT_ACCEPTABLE);
                    }
                }

                newAccount.setStatus(Status.ACTIVE);

                newAccount.setPrimaryOwner(primaryOwner.get());
                if (passedObject.getSecondaryOwnerName() != null && passedObject.getSecondaryOwnerName().isPresent()) {

                    Optional<AccountHolder> secondaryOwner = accountHolderRepository.findByName(passedObject.getSecondaryOwnerName().get());
                    secondaryOwner.ifPresent(newAccount::setSecondaryOwner);
                }

                if (passedObject.getInterestRate() != null && passedObject.getInterestRate().isPresent()) {
                    if (GenericValidator.isDouble(passedObject.getInterestRate().get())) {
                        if (GenericValidator.isInRange(Double.parseDouble(passedObject.getInterestRate().get()), 0.100000, 0.20000)) {
                            newAccount.setInterestRate(new BigDecimal(passedObject.getInterestRate().get()));
                        } else {
                            return new ResponseEntity<>("Interest rate must be in range from 0.1 to 0.2",
                                    HttpStatus.NOT_ACCEPTABLE);
                        }

                    } else {
                        return new ResponseEntity<>("Invalid input for interest rate. Please provide numeric data",
                                HttpStatus.NOT_ACCEPTABLE);
                    }
                } else {
                    newAccount.setInterestRate(new BigDecimal(0.0025));
                }


                if (passedObject.getCreditLimit() != null && passedObject.getCreditLimit().isPresent()) {
                    if (GenericValidator.isDouble(passedObject.getCreditLimit().get())) {
                        if (GenericValidator.isInRange(Double.parseDouble(passedObject.getCreditLimit().get()), 100, 100000)) {
                            newAccount.setCreditLimit(new Money(new BigDecimal(passedObject.getCreditLimit().get())));
                        } else {
                            return new ResponseEntity<>("Credit limit must be in range from 100 to 100 000",
                                    HttpStatus.NOT_ACCEPTABLE);
                        }
                    } else {
                        return new ResponseEntity<>("Invalid input for credit limit. Please provide numeric data",
                                HttpStatus.NOT_ACCEPTABLE);
                    }
                } else {
                    newAccount.setCreditLimit(new Money(new BigDecimal(100)));
                }

                newAccount.setPenaltyFee(new Money(new BigDecimal(40)));

                creditCardRepository.save(newAccount);

        return new ResponseEntity<>("New credit card account created",
                HttpStatus.CREATED);
    }

    public ResponseEntity<?> storeAny(@Valid CreateAccountDTO passedObject) {

        //check if primary owner is provided
        if (GenericValidator.isBlankOrNull(passedObject.getPrimaryOwnerName())) {
            return new ResponseEntity<>("Primary owner must be specified.", HttpStatus.NOT_ACCEPTABLE);
        }
        Optional<AccountHolder> primaryOwner = accountHolderRepository.findByName(passedObject.getPrimaryOwnerName());
        if (primaryOwner.isEmpty()) {
            return new ResponseEntity<>("User with name ".concat(passedObject.getPrimaryOwnerName()).concat(" does not exist! Please create user first!"),
                    HttpStatus.NOT_ACCEPTABLE);
        }

        //Check if given accountType exists
        if (GenericValidator.isBlankOrNull(passedObject.getAccountType())) {
            return new ResponseEntity<>("Account type must be specified.", HttpStatus.NOT_ACCEPTABLE);
        }
        String  accountType  = passedObject.getAccountType().toUpperCase().replaceAll("\\s+", "");
        boolean validAccount = false;
        for (AccountType a : AccountType.values()) {
            if (accountType.equalsIgnoreCase(a.toString())) {
                validAccount = true;
            }
        }
        if (!validAccount) {
            return new ResponseEntity<>("Account of type: ".concat(accountType.toString()).concat(" does not exist."),
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (accountType.equals("CHECKING")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            formatter = formatter.withLocale(Locale.ENGLISH);
            LocalDate birthDate = LocalDate.parse(primaryOwner.get().getDateOfBirth(), formatter);
            LocalDate today     = LocalDate.now();
            int       years     = Period.between(birthDate, today).getYears();
            if (years < 24) {
                accountType = "STUDENT_CHECKING";
            }

        }

        switch (accountType) {
            case "SAVINGS": {
                Savings newAccount = new Savings();
                if (passedObject.getBalance().isEmpty()) {
                    newAccount.setBalance(new Money(new BigDecimal(100)));
                } else if (!GenericValidator.isDouble(passedObject.getBalance().get()) || Long.parseLong(passedObject.getBalance().get()) < Long.parseLong("100")) {
                    return new ResponseEntity<>("Starting balance for savings account must be numeric and cannot be lower than minimum balance.",
                            HttpStatus.NOT_ACCEPTABLE);
                } else {
                    try {
                        newAccount.setBalance(new Money(new BigDecimal(String.valueOf(passedObject.getBalance().get()))));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseEntity<>("Minimum balance must be provided.",
                                HttpStatus.NOT_ACCEPTABLE);
                    }

                }

//                var salt     = Passwords.getNextSalt();
//                var password = primaryOwner.get().getPassword().toCharArray();
//                var secretKey=Passwords.hash(password, salt);
//                newAccount.setSecretKey(secretKey);
//                newAccount.setSalt(salt);

                newAccount.setPenaltyFee(new Money(BigDecimal.valueOf(40)));

                if (GenericValidator.isBlankOrNull(passedObject.getCreationDate().get())) {
                    newAccount.setCreationDate(new Date());
                } else {
                    if (GenericValidator.isDate(passedObject.getCreationDate().get(), "dd-MM-yyyy", true)) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                        try {
                            newAccount.setCreationDate(formatter.parse(passedObject.getCreationDate().get()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new ResponseEntity<>("Invalid creation date format. Please please provide date in dd-MM-yyyy format",
                                    HttpStatus.NOT_ACCEPTABLE);
                        }
                    } else {
                        return new ResponseEntity<>("Invalid creation date format. Please please provide date in dd-MM-yyyy format",
                                HttpStatus.NOT_ACCEPTABLE);
                    }
                }

                newAccount.setStatus(Status.ACTIVE);

                newAccount.setPrimaryOwner(primaryOwner.get());
                if (passedObject.getSecondaryOwnerName() != null && passedObject.getSecondaryOwnerName().isPresent()) {

                    Optional<AccountHolder> secondaryOwner = accountHolderRepository.findByName(passedObject.getSecondaryOwnerName().get());
                    secondaryOwner.ifPresent(newAccount::setSecondaryOwner);
                }


                if (passedObject.getInterestRate() != null && passedObject.getInterestRate().isPresent()) {
                    if (GenericValidator.isDouble(passedObject.getInterestRate().get())) {
                        newAccount.setInterestRate(new BigDecimal(passedObject.getInterestRate().get()));
                    } else {
                        return new ResponseEntity<>("Invalid input for interest rate. Please provide numeric data",
                                HttpStatus.NOT_ACCEPTABLE);
                    }
                } else {
                    newAccount.setInterestRate(new BigDecimal(0.0025));
                }

                if (passedObject.getMinimumBalance() != null && passedObject.getMinimumBalance().isPresent()) {
                    if (GenericValidator.isDouble(passedObject.getMinimumBalance().get())) {
                        newAccount.setMinimumBalance(new Money(new BigDecimal(passedObject.getMinimumBalance().get())));
                    } else {
                        return new ResponseEntity<>("Invalid input for minimum balance. Please provide numeric data",
                                HttpStatus.NOT_ACCEPTABLE);
                    }
                } else {
                    newAccount.setMinimumBalance(new Money(new BigDecimal(1000)));
                }

                newAccount.setPenaltyFee(new Money(new BigDecimal(40)));

                savingsRepository.save(newAccount);
                break;
            }

            case "CHECKING": {
                Checking newAccount = new Checking();
                if (passedObject.getBalance().isEmpty()) {
                    newAccount.setBalance(new Money(new BigDecimal(100)));
                } else if (!GenericValidator.isDouble(passedObject.getBalance().get()) || Long.parseLong(passedObject.getBalance().get()) < Long.parseLong("100")) {
                    return new ResponseEntity<>("Starting balance for checking account cannot be lower than minimum balance.",
                            HttpStatus.NOT_ACCEPTABLE);
                } else {
                    try {
                        newAccount.setBalance(new Money(new BigDecimal(String.valueOf(passedObject.getBalance().get()))));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseEntity<>("Balance must be provided as a valid number.",
                                HttpStatus.NOT_ACCEPTABLE);
                    }

                }

//                var salt     = Passwords.getNextSalt();
//                var password = primaryOwner.get().getPassword().toCharArray();
//                var secretKey=Passwords.hash(password, salt);
//                newAccount.setSecretKey(secretKey);
//                newAccount.setSalt(salt);

                newAccount.setPenaltyFee(new Money(BigDecimal.valueOf(40)));

                if (GenericValidator.isBlankOrNull(passedObject.getCreationDate().get())) {
                    newAccount.setCreationDate(new Date());
                } else {
                    if (GenericValidator.isDate(passedObject.getCreationDate().get(), "dd-MM-yyyy", true)) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                        try {
                            newAccount.setCreationDate(formatter.parse(passedObject.getCreationDate().get()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new ResponseEntity<>("Invalid creation date format. Please please provide date in dd-MM-yyyy format",
                                    HttpStatus.NOT_ACCEPTABLE);
                        }
                    } else {
                        return new ResponseEntity<>("Invalid creation date format. Please please provide date in dd-MM-yyyy format",
                                HttpStatus.NOT_ACCEPTABLE);
                    }
                }

                newAccount.setStatus(Status.ACTIVE);

                newAccount.setPrimaryOwner(primaryOwner.get());
                if (passedObject.getSecondaryOwnerName() != null && passedObject.getSecondaryOwnerName().isPresent()) {

                    Optional<AccountHolder> secondaryOwner = accountHolderRepository.findByName(passedObject.getSecondaryOwnerName().get());
                    secondaryOwner.ifPresent(newAccount::setSecondaryOwner);
                }


                if (passedObject.getMonthlyMaintenanceFee() != null && passedObject.getMonthlyMaintenanceFee().isPresent()) {
                    if (GenericValidator.isDouble(passedObject.getMonthlyMaintenanceFee().get())) {
                        newAccount.setMonthlyMaintenanceFee(new Money(new BigDecimal(passedObject.getMonthlyMaintenanceFee().get())));
                    } else {
                        return new ResponseEntity<>("Invalid input for Monthly Maintenance Fee. Please provide numeric data",
                                HttpStatus.NOT_ACCEPTABLE);
                    }
                } else {
                    newAccount.setMonthlyMaintenanceFee(new Money(new BigDecimal(12)));
                }

                if (passedObject.getMinimumBalance() != null && passedObject.getMinimumBalance().isPresent()) {
                    if (GenericValidator.isDouble(passedObject.getMinimumBalance().get())) {
                        newAccount.setMinimumBalance(new Money(new BigDecimal(passedObject.getMinimumBalance().get())));
                    } else {
                        return new ResponseEntity<>("Invalid input for minimum balance. Please provide numeric data",
                                HttpStatus.NOT_ACCEPTABLE);
                    }
                } else {
                    newAccount.setMinimumBalance(new Money(new BigDecimal(250)));
                }

                newAccount.setPenaltyFee(new Money(new BigDecimal(40)));

                checkingRepository.save(newAccount);
                break;
            }


            case "STUDENT_CHECKING": {
                StudentChecking newAccount = new StudentChecking();
                if (passedObject.getBalance().isEmpty()) {
                    newAccount.setBalance(new Money(new BigDecimal(0)));
                } else if (!GenericValidator.isDouble(passedObject.getBalance().get()) || Long.parseLong(passedObject.getBalance().get()) < Long.parseLong("0")) {
                    return new ResponseEntity<>("Starting balance for student checking account cannot be lower than 0.",
                            HttpStatus.NOT_ACCEPTABLE);
                } else {
                    try {
                        newAccount.setBalance(new Money(new BigDecimal(String.valueOf(passedObject.getBalance().get()))));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseEntity<>("Balance must be provided as a valid number.",
                                HttpStatus.NOT_ACCEPTABLE);
                    }

                }

//                var salt     = Passwords.getNextSalt();
//                var password = primaryOwner.get().getPassword().toCharArray();
//                var secretKey=Passwords.hash(password, salt);
//                newAccount.setSecretKey(secretKey);
//                newAccount.setSalt(salt);

                newAccount.setPenaltyFee(new Money(BigDecimal.valueOf(40)));

                if (GenericValidator.isBlankOrNull(passedObject.getCreationDate().get())) {
                    newAccount.setCreationDate(new Date());
                } else {
                    if (GenericValidator.isDate(passedObject.getCreationDate().get(), "dd-MM-yyyy", true)) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                        try {
                            newAccount.setCreationDate(formatter.parse(passedObject.getCreationDate().get()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new ResponseEntity<>("Invalid creation date format. Please please provide date in dd-MM-yyyy format",
                                    HttpStatus.NOT_ACCEPTABLE);
                        }
                    } else {
                        return new ResponseEntity<>("Invalid creation date format. Please please provide date in dd-MM-yyyy format",
                                HttpStatus.NOT_ACCEPTABLE);
                    }
                }

                newAccount.setStatus(Status.ACTIVE);

                newAccount.setPrimaryOwner(primaryOwner.get());
                if (passedObject.getSecondaryOwnerName() != null && passedObject.getSecondaryOwnerName().isPresent()) {

                    Optional<AccountHolder> secondaryOwner = accountHolderRepository.findByName(passedObject.getSecondaryOwnerName().get());
                    secondaryOwner.ifPresent(newAccount::setSecondaryOwner);
                }

                newAccount.setPenaltyFee(new Money(new BigDecimal(40)));

                studentCheckingRepository.save(newAccount);
                break;
            }

            case "CREDIT_CARD": {
                CreditCard newAccount = new CreditCard();
                if (passedObject.getBalance().isEmpty()) {
                    newAccount.setBalance(new Money(new BigDecimal(0)));
                } else if (!GenericValidator.isDouble(passedObject.getBalance().get()) || Long.parseLong(passedObject.getBalance().get()) < Long.parseLong("100")) {
                    return new ResponseEntity<>("Starting balance for credit card account cannot be lower than credit.",
                            HttpStatus.NOT_ACCEPTABLE);
                } else {
                    try {
                        newAccount.setBalance(new Money(new BigDecimal(String.valueOf(passedObject.getBalance().get()))));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseEntity<>("Balance must be provided as a valid number.",
                                HttpStatus.NOT_ACCEPTABLE);
                    }

                }

//                var salt     = Passwords.getNextSalt();
//                var password = primaryOwner.get().getPassword().toCharArray();
//                var secretKey=Passwords.hash(password, salt);
//                newAccount.setSecretKey(secretKey);
//                newAccount.setSalt(salt);

                newAccount.setPenaltyFee(new Money(BigDecimal.valueOf(40)));

                if (GenericValidator.isBlankOrNull(passedObject.getCreationDate().get())) {
                    newAccount.setCreationDate(new Date());
                } else {
                    if (GenericValidator.isDate(passedObject.getCreationDate().get(), "dd-MM-yyyy", true)) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                        try {
                            newAccount.setCreationDate(formatter.parse(passedObject.getCreationDate().get()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new ResponseEntity<>("Invalid creation date format. Please please provide date in dd-MM-yyyy format",
                                    HttpStatus.NOT_ACCEPTABLE);
                        }
                    } else {
                        return new ResponseEntity<>("Invalid creation date format. Please please provide date in dd-MM-yyyy format",
                                HttpStatus.NOT_ACCEPTABLE);
                    }
                }

                newAccount.setStatus(Status.ACTIVE);

                newAccount.setPrimaryOwner(primaryOwner.get());
                if (passedObject.getSecondaryOwnerName() != null && passedObject.getSecondaryOwnerName().isPresent()) {

                    Optional<AccountHolder> secondaryOwner = accountHolderRepository.findByName(passedObject.getSecondaryOwnerName().get());
                    secondaryOwner.ifPresent(newAccount::setSecondaryOwner);
                }

                if (passedObject.getInterestRate() != null && passedObject.getInterestRate().isPresent()) {
                    if (GenericValidator.isDouble(passedObject.getInterestRate().get())) {
                        if (GenericValidator.isInRange(Double.parseDouble(passedObject.getInterestRate().get()), 0.100000, 0.20000)) {
                            newAccount.setInterestRate(new BigDecimal(passedObject.getInterestRate().get()));
                        } else {
                            return new ResponseEntity<>("Interest rate must be in range from 0.1 to 0.2",
                                    HttpStatus.NOT_ACCEPTABLE);
                        }

                    } else {
                        return new ResponseEntity<>("Invalid input for interest rate. Please provide numeric data",
                                HttpStatus.NOT_ACCEPTABLE);
                    }
                } else {
                    newAccount.setInterestRate(new BigDecimal(0.0025));
                }


                if (passedObject.getCreditLimit() != null && passedObject.getCreditLimit().isPresent()) {
                    if (GenericValidator.isDouble(passedObject.getCreditLimit().get())) {
                        if (GenericValidator.isInRange(Double.parseDouble(passedObject.getCreditLimit().get()), 100, 100000)) {
                            newAccount.setCreditLimit(new Money(new BigDecimal(passedObject.getCreditLimit().get())));
                        } else {
                            return new ResponseEntity<>("Credit limit must be in range from 100 to 100 000",
                                    HttpStatus.NOT_ACCEPTABLE);
                        }
                    } else {
                        return new ResponseEntity<>("Invalid input for credit limit. Please provide numeric data",
                                HttpStatus.NOT_ACCEPTABLE);
                    }
                } else {
                    newAccount.setCreditLimit(new Money(new BigDecimal(100)));
                }

                newAccount.setPenaltyFee(new Money(new BigDecimal(40)));

                creditCardRepository.save(newAccount);
                break;
            }
        }


        return new ResponseEntity<>("New ".concat(accountType).concat(" account created"),
                HttpStatus.CREATED);
    }
}

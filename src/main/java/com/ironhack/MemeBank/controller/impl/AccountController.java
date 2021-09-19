package com.ironhack.MemeBank.controller.impl;

import com.ironhack.MemeBank.dao.Money;
import com.ironhack.MemeBank.dao.Role;
import com.ironhack.MemeBank.dao.accounts.Account;
import com.ironhack.MemeBank.dao.accounts.Savings;
import com.ironhack.MemeBank.dao.users.AccountHolder;
import com.ironhack.MemeBank.dao.users.Admin;
import com.ironhack.MemeBank.dao.users.ThirdParty;
import com.ironhack.MemeBank.dao.users.User;
import com.ironhack.MemeBank.dto.CreateAccountDTO;
import com.ironhack.MemeBank.dto.CreateUserDTO;
import com.ironhack.MemeBank.enums.AccountType;
import com.ironhack.MemeBank.enums.RoleType;
import com.ironhack.MemeBank.enums.Status;
import com.ironhack.MemeBank.repository.*;
import com.ironhack.MemeBank.security.Passwords;
import com.ironhack.MemeBank.security.SecretKey;
import com.ironhack.MemeBank.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
public class AccountController {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

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
    StudentCheckingRepository studentCheckingtRepository;

    @Autowired
    SavingsRepository savingsRepository;

    @Autowired
    CreditCardRepository creditCardRepository;



    @GetMapping("/accounts")
    @ResponseStatus(HttpStatus.OK)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @PostMapping("/accounts")
    public ResponseEntity<?> store(@RequestBody @Valid CreateAccountDTO passedObject) {

        if (passedObject.getPrimaryOwnerName()==null || passedObject.getPrimaryOwnerName().isEmpty()){
            return new ResponseEntity<>("Primary owner must be specified.",HttpStatus.NOT_ACCEPTABLE);}

        Optional<AccountHolder> primaryOwner = accountHolderRepository.findByName(passedObject.getPrimaryOwnerName());




        //Check if given accountType exists
        if(passedObject.getAccountType()==null || passedObject.getAccountType().isEmpty()){
            return new ResponseEntity<>("Account type must be specified.",HttpStatus.NOT_ACCEPTABLE);}

        String         accountType      =passedObject.getAccountType().toUpperCase().replaceAll("\\s+","");
        boolean validAccount=false;
        for (AccountType a : AccountType.values())
        { if (accountType.equalsIgnoreCase(a.toString())){ validAccount=true;}
        }
        if(!validAccount) {
            return new ResponseEntity<>("Account of type: ".concat(accountType.toString()).concat(" does not exist."),
                    HttpStatus.NOT_ACCEPTABLE);
        }else{


            //check if primary owner exists
        if(primaryOwner.isEmpty()){
            return new ResponseEntity<>("User with name ".concat(passedObject.getPrimaryOwnerName()).concat(" does not exist! Please create user first!"),
                    HttpStatus.NOT_ACCEPTABLE);
        }{

        switch(accountType) {
            case "SAVINGS": {
                Savings newAccount = new Savings();
                if (passedObject.getBalance().isEmpty()) {
                    newAccount.setBalance(new Money(new BigDecimal(100)));
                }else if(Long.parseLong(passedObject.getBalance().get())<Long.parseLong("100")){
                    return new ResponseEntity<>("Starting balance for savings account cannot be lower than minimum balance.",
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

                if(passedObject.getCreationDate()==null || passedObject.getCreationDate().isEmpty()) {
                    newAccount.setCreationDate(new Date());
                }else{

                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

                    try {
                        Date date = formatter.parse(passedObject.getCreationDate().get());
                        newAccount.setCreationDate(date);
                    }catch (ParseException e) {
                        e.printStackTrace();
                        return new ResponseEntity<>("Invalid creation date format. Please please provide date in dd-MM-yyyy format",
                                HttpStatus.NOT_ACCEPTABLE);
                    }
                }

                newAccount.setStatus(Status.ACTIVE);

                newAccount.setPrimaryOwner(primaryOwner.get());
                if (passedObject.getSecondaryOwnerName()!=null && passedObject.getSecondaryOwnerName().isPresent()){

                    Optional<AccountHolder> secondaryOwner = accountHolderRepository.findByName(passedObject.getSecondaryOwnerName().get());
                    secondaryOwner.ifPresent(newAccount::setSecondaryOwner);
                }


                if(passedObject.getInterestRate()!=null && passedObject.getInterestRate().isPresent()){
                    newAccount.setInterestRate(new BigDecimal(passedObject.getInterestRate().get()));
                }else{
                    newAccount.setInterestRate(new BigDecimal(0.0025));
                }

                if(passedObject.getMinimumBalance()!=null && passedObject.getMinimumBalance().isPresent()){
                    newAccount.setMinimumBalance(new Money(new BigDecimal(passedObject.getMinimumBalance().get())));
                }else{
                    newAccount.setMinimumBalance(new Money(new BigDecimal(1000)));
                }

                newAccount.setPenaltyFee(new Money(new BigDecimal(40)));

                savingsRepository.save(newAccount);
                break;
            }
            }

            }
        }

        return new ResponseEntity<>("New ".concat(accountType).concat(" account created"),
                HttpStatus.CREATED);
    }

}

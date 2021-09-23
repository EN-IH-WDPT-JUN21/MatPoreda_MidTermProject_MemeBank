package com.ironhack.MemeBank.controller.impl;

import com.ironhack.MemeBank.dao.Money;
import com.ironhack.MemeBank.dao.Role;
import com.ironhack.MemeBank.dao.accounts.*;
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
import com.ironhack.MemeBank.service.impl.AccountService;
import com.ironhack.MemeBank.service.impl.UserServiceImpl;
import com.ironhack.MemeBank.service.interfaces.UserService;
import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class AccountController {

    @Autowired
    AccountHolderRepository accountHolderRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    UserRepository userRepository;



    @GetMapping("/accounts")
    @ResponseStatus(HttpStatus.OK)
    public List<Account> getAccounts() {
        return accountRepository.findByPrimaryOwnerOrSecondOwner(String.valueOf(userRepository.findByUsername(userServiceImpl.getCurrentUsername()).get()));
    }

    @GetMapping("/accounts/all")
    @ResponseStatus(HttpStatus.OK)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @PostMapping("/accounts")
    public ResponseEntity<?> store(@RequestBody CreateAccountDTO passedObject) {

        if(Objects.isNull(passedObject)){
            return new ResponseEntity<>("Post request must provide valid body.",HttpStatus.NOT_ACCEPTABLE);
        }

        //check if primary owner is provided
        if (GenericValidator.isBlankOrNull(passedObject.getPrimaryOwnerName())){
            return new ResponseEntity<>("Primary owner must be specified.",HttpStatus.NOT_ACCEPTABLE);}
        Optional<AccountHolder> primaryOwner = accountHolderRepository.findByName(passedObject.getPrimaryOwnerName());
        if(primaryOwner.isEmpty()){
            return new ResponseEntity<>("User with name ".concat(passedObject.getPrimaryOwnerName()).concat(" does not exist! Please create user first!"),
                    HttpStatus.NOT_ACCEPTABLE);
        }

        //Check if given accountType exists
        if(GenericValidator.isBlankOrNull(passedObject.getAccountType())){
            return new ResponseEntity<>("Account type must be specified.",HttpStatus.NOT_ACCEPTABLE);
        }
        String accountType =passedObject.getAccountType().toUpperCase().replaceAll("\\s+","");
        boolean validAccount=false;
        for (AccountType a : AccountType.values())
        { if (accountType.equalsIgnoreCase(a.toString())){ validAccount=true;}
        }
        if(!validAccount) {
            return new ResponseEntity<>("Account of type: ".concat(accountType.toString()).concat(" does not exist."),
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if(accountType.equals("CHECKING") || accountType.equals("STUDENT_CHECKING")){
            accountType="CHECKING";
        }

        switch(accountType) {
            case "SAVINGS": {
                return accountService.storeSavings(passedObject);
            }

            case "CHECKING": {
                return accountService.storeChecking(passedObject);
            }

            case "CREDIT_CARD": {
                return accountService.storeCreditCard(passedObject);
            }
            }
        return new ResponseEntity<>("New ".concat(accountType).concat(" account created"),
                HttpStatus.CREATED);
    }

}

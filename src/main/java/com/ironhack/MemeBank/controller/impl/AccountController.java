package com.ironhack.MemeBank.controller.impl;

import com.ironhack.MemeBank.dao.Money;
import com.ironhack.MemeBank.dao.accounts.Account;
import com.ironhack.MemeBank.dao.users.AccountHolder;
import com.ironhack.MemeBank.dto.CreateAccountDTO;
import com.ironhack.MemeBank.dto.TransactionDTO;
import com.ironhack.MemeBank.enums.AccountType;
import com.ironhack.MemeBank.repository.AccountHolderRepository;
import com.ironhack.MemeBank.repository.AccountRepository;
import com.ironhack.MemeBank.repository.AdminRepository;
import com.ironhack.MemeBank.repository.UserRepository;
import com.ironhack.MemeBank.service.impl.AccountService;
import com.ironhack.MemeBank.service.impl.UserServiceImpl;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    @Autowired
    AdminRepository adminRepository;


    @GetMapping("/accounts")
    @ResponseStatus(HttpStatus.OK)
    public List<Account> getAccounts() {
//        if(adminRepository.findByUsername(userServiceImpl.getCurrentUsername()).isPresent())
        return accountRepository.findByPrimaryOwnerOrSecondaryOwner(userRepository.findByUsername(userServiceImpl.getCurrentUsername()).get(), userRepository.findByUsername(userServiceImpl.getCurrentUsername()).get());
    }

    @GetMapping("/accounts/all")
    @ResponseStatus(HttpStatus.OK)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @PostMapping("/accounts")
    public ResponseEntity<?> store(@RequestBody CreateAccountDTO passedObject) {

        if (Objects.isNull(passedObject)) {
            return new ResponseEntity<>("Post request must provide valid body.", HttpStatus.NOT_ACCEPTABLE);
        }

        //check if primary owner is provided
        if (GenericValidator.isBlankOrNull(passedObject.getPrimaryOwnerName())) {
            return new ResponseEntity<>("primaryOwnerName must be specified.", HttpStatus.NOT_ACCEPTABLE);
        }
        Optional<AccountHolder> primaryOwner = accountHolderRepository.findByUsername(passedObject.getPrimaryOwnerName());
        if (primaryOwner.isEmpty()) {
            return new ResponseEntity<>("User with name ".concat(passedObject.getPrimaryOwnerName()).concat(" does not exist! Please create user first!"),
                    HttpStatus.NOT_ACCEPTABLE);
        }

        //Check if given accountType exists
        if (GenericValidator.isBlankOrNull(passedObject.getAccountType())) {
            return new ResponseEntity<>("Type for account must be specified.", HttpStatus.NOT_ACCEPTABLE);
        }
        String  accountType  = passedObject.getAccountType().toUpperCase().replaceAll("\\s+", "");
        boolean validAccount = false;
        for (AccountType a : AccountType.values()) {
            if (accountType.equalsIgnoreCase(a.toString())) {
                validAccount = true;
            }
        }
        if (!validAccount) {
            return new ResponseEntity<>("Account of type: ".concat(accountType).concat(" does not exist."),
                    HttpStatus.NOT_ACCEPTABLE);
        }

        if (accountType.equals("CHECKING") || accountType.equals("STUDENT_CHECKING")) {
            accountType = "CHECKING";
        }

        switch (accountType) {
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

    @PostMapping("/accounts/set_balance/{id}")
    public ResponseEntity<?> setBalance(@RequestBody TransactionDTO passedObject, @PathVariable(name = "id") String accountId) {

        if (GenericValidator.isBlankOrNull(accountId) || !GenericValidator.isLong(accountId)) {
            return new ResponseEntity<>("AccountId must be provided as a valid long",
                    HttpStatus.NOT_ACCEPTABLE);
        } else if (accountRepository.findById(Long.valueOf(accountId)).isEmpty()) {
            return new ResponseEntity<>("AccountId not found",
                    HttpStatus.NOT_ACCEPTABLE);
        } else if (GenericValidator.isBlankOrNull(String.valueOf(passedObject.getBalance())) || !GenericValidator.isDouble(passedObject.getBalance())) {
            return new ResponseEntity<>("Balance must be provided as a valid double",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        Account account = accountRepository.findById(Long.valueOf(accountId)).get();
        account.setBalance(new Money(new BigDecimal(passedObject.getBalance())));
        accountRepository.save(account);
        return new ResponseEntity<>("Account balance successfully changed",
                HttpStatus.OK);
    }
}

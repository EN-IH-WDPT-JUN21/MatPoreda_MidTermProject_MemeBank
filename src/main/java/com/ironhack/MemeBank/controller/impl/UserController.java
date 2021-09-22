package com.ironhack.MemeBank.controller.impl;

import com.ironhack.MemeBank.dao.Role;
import com.ironhack.MemeBank.dao.users.AccountHolder;
import com.ironhack.MemeBank.dao.users.Admin;
import com.ironhack.MemeBank.dao.users.ThirdParty;
import com.ironhack.MemeBank.dao.users.User;
import com.ironhack.MemeBank.dto.CreateUserDTO;
import com.ironhack.MemeBank.enums.RoleType;
import com.ironhack.MemeBank.repository.*;
import com.ironhack.MemeBank.service.impl.UserServiceImpl;
import com.ironhack.MemeBank.service.interfaces.UserService;
import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/admins")
    @ResponseStatus(HttpStatus.OK)
    public List<? extends User> getAdmins() {
        return adminRepository.findAll();
    }

    @GetMapping("/users/third_party")
    @ResponseStatus(HttpStatus.OK)
    public List<? extends User> getThirdParty() {
        return thirdPartyRepository.findAll();
    }

    @GetMapping("/users/account_holders")
    @ResponseStatus(HttpStatus.OK)
    public List<? extends User> getAccountHolders() {
        return accountHolderRepository.findAll();
    }

    @PostMapping("/users")
    public ResponseEntity<?> store(@RequestBody CreateUserDTO passedObject) {
        if(Objects.isNull(passedObject)){
            return new ResponseEntity<>("Post request must provide valid body.",HttpStatus.NOT_ACCEPTABLE);
        }
        return userServiceImpl.storeAny(passedObject);
    }
}

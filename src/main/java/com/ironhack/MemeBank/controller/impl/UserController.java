package com.ironhack.MemeBank.controller.impl;

import com.ironhack.MemeBank.dao.Role;
import com.ironhack.MemeBank.dao.users.AccountHolder;
import com.ironhack.MemeBank.dao.users.Admin;
import com.ironhack.MemeBank.dao.users.User;
import com.ironhack.MemeBank.enums.RoleType;
import com.ironhack.MemeBank.repository.*;
import com.ironhack.MemeBank.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AdminRepository adminRepository;

//    @Autowired
//    ThirdPartyRepository thirdPartyRepository;

    @Autowired
    AccountHolderRepository accountHolderRepository;

    @PostMapping("/admin")
    public ResponseEntity<?> store(@RequestBody Admin admin) {
        String         role      ="ADMIN";
        Optional<User> localUser = userRepository.findByUsername(admin.getUsername());
        Optional<Role> localRole =roleRepository.findByName(role);
        if(localUser.isPresent()){
            return new ResponseEntity<>("User ".concat(admin.getUsername()).concat(" already exists!"),
                    HttpStatus.CONFLICT);
        }

        boolean validRole=false;

        for (RoleType r : RoleType.values())
        { if (role.equalsIgnoreCase(r.toString())){ validRole=true;}
        }
        if(!validRole) {
            return new ResponseEntity<>("Role ".concat(role.toString()).concat(" does not exist."),
                    HttpStatus.NOT_ACCEPTABLE);
        }else{
            Admin localAdmin=new Admin();
            localAdmin.setUsername(admin.getUsername());
            localAdmin.setPassword(admin.getPassword());

            if (roleRepository.findByName(role).isPresent()){
                localAdmin.setRole(roleRepository.findByName(role).get());
            }else{
                Role newAdminRole =new Role(role.toUpperCase());
                roleRepository.save(newAdminRole);
                localAdmin.setRole(newAdminRole);
            }
            adminRepository.save(localAdmin);
        }
        return new ResponseEntity<>("New ".concat(role.toUpperCase()).concat(" created"),
                HttpStatus.CREATED);
    }

    @PostMapping("/account_holder")
    public ResponseEntity<?> store(@RequestBody AccountHolder aHolder) {
        String role="ACCOUNT_HOLDER";
        Optional<User> localUser = userRepository.findByUsername(aHolder.getUsername());
        Optional<Role> localRole=roleRepository.findByName(role);
        if(localUser.isPresent()){
            return new ResponseEntity<>("User ".concat(aHolder.getUsername()).concat(" already exists!"),
                    HttpStatus.CONFLICT);
        }
        boolean validRole=false;
        for (RoleType r : RoleType.values())
        { if (role.equalsIgnoreCase(r.toString())){ validRole=true;}
        }
        if(!validRole) {
            return new ResponseEntity<>("Role ".concat(role.toString()).concat(" does not exist."),
                    HttpStatus.NOT_ACCEPTABLE);
        }else{
            AccountHolder localAccountHolder=new AccountHolder();
            localAccountHolder.setUsername(aHolder.getUsername());
            localAccountHolder.setPassword(aHolder.getPassword());
            localAccountHolder.setName(aHolder.getName());
            localAccountHolder.setDateOfBirth(aHolder.getDateOfBirth());
            localAccountHolder.setPrimaryAddress(aHolder.getPrimaryAddress());
            localAccountHolder.setMailingAddress(aHolder.getMailingAddress());

            if (roleRepository.findByName(role).isPresent()){
                localAccountHolder.setRole(roleRepository.findByName(role).get());
            }else{
                Role newAHolderRole =new Role(role.toUpperCase());
                roleRepository.save(newAHolderRole);
                localAccountHolder.setRole(newAHolderRole);
            }
            accountHolderRepository.save(localAccountHolder);
        }
        return new ResponseEntity<>("New ".concat(role.toUpperCase()).concat(" created"),
                HttpStatus.CREATED);
    }

//    @PostMapping("/third_party")
//    public ResponseEntity<?> store(@RequestBody ThirdParty tParty) {
//        String role="THIRD_PARTY";
//        Optional<User> localUser = userRepository.findByUsername(tParty.getUsername());
//        Optional<Role> localRole=roleRepository.findByName(role);
//        if(localUser.isPresent()){
//            return new ResponseEntity<>("User ".concat(tParty.getUsername()).concat(" already exists!"),
//                    HttpStatus.CONFLICT);
//        }
//        boolean validRole=false;
//        for (RoleType r : RoleType.values())
//        { if (role.equalsIgnoreCase(r.toString())){ validRole=true;}
//        }
//        if(!validRole) {
//            return new ResponseEntity<>("Role ".concat(role.toString()).concat(" does not exist."),
//                    HttpStatus.NOT_ACCEPTABLE);
//        }else{
//            ThirdParty localTParty=new ThirdParty();
//            localTParty.setUsername(tParty.getUsername());
//            localTParty.setPassword(tParty.getPassword());
//
//            if (roleRepository.findByName(role).isPresent()){
//                localTParty.setRole(roleRepository.findByName(role).get());
//            }else{
//                Role newTPartyRole =new Role(role.toUpperCase());
//                roleRepository.save(newTPartyRole);
//                localTParty.setRole(newTPartyRole);
//            }
//            thirdPartyRepository.save(localTParty);
//        }
//        return new ResponseEntity<>("New ".concat(role.toUpperCase()).concat(" created"),
//                HttpStatus.CREATED);
//    }


    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


}

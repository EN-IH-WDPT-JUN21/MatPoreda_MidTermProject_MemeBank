package com.ironhack.MemeBank.controller.impl;

import com.ironhack.MemeBank.dao.Role;
import com.ironhack.MemeBank.dao.users.AccountHolder;
import com.ironhack.MemeBank.dao.users.Admin;
import com.ironhack.MemeBank.dao.users.ThirdParty;
import com.ironhack.MemeBank.dao.users.User;
import com.ironhack.MemeBank.dto.CreateUserDTO;
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

    @Autowired
    ThirdPartyRepository thirdPartyRepository;

    @Autowired
    AccountHolderRepository accountHolderRepository;

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/users")
    public ResponseEntity<?> store(@RequestBody CreateUserDTO passedObject) {
        String         role      =passedObject.getRoleType().toUpperCase().replaceAll("\\s+","");
        Optional<User> localUser = userRepository.findByUsername(passedObject.getUsername());
        Role verifiedRole;

        //check if password or username are empty
        if(passedObject.getUsername().isEmpty() || passedObject.getPassword().isEmpty()){
            return new ResponseEntity<>("Username and password cannot be empty!",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        //Check if user already exists
        if(userRepository.findByUsername(passedObject.getUsername()).isPresent()){
            return new ResponseEntity<>("User ".concat(passedObject.getUsername()).concat(" already exists!"),
                    HttpStatus.CONFLICT);
        }

        //Check if given role exists, if not validate enum and create one
        boolean validRole=false;
        for (RoleType r : RoleType.values())
        { if (role.equalsIgnoreCase(r.toString())){ validRole=true;}
        }
        if(!validRole) {
            return new ResponseEntity<>("Role ".concat(role.toString()).concat(" does not exist."),
                    HttpStatus.NOT_ACCEPTABLE);
        }else{
            if (roleRepository.findByName(role).isPresent()){
                verifiedRole=roleRepository.findByName(role).get();
            }else{
                verifiedRole =new Role(role.toUpperCase());
                roleRepository.save(verifiedRole);
            }

            //create specific users

            switch(role){
                case "ADMIN":{
                    Admin localAdmin=new Admin();
                    localAdmin.setUsername(passedObject.getUsername());
                    localAdmin.setPassword(passedObject.getPassword());
                    localAdmin.setRole(verifiedRole);
                    adminRepository.save(localAdmin);
                    break;
                }

                case "ACCOUNT_HOLDER":{
                    AccountHolder localAccountHolder=new AccountHolder();
                    localAccountHolder.setUsername(passedObject.getUsername());
                    localAccountHolder.setPassword(passedObject.getPassword());

                    if(passedObject.getName().isEmpty()){
                        return new ResponseEntity<>("Username and password cannot be empty!", HttpStatus.NOT_ACCEPTABLE);
                    } else{localAccountHolder.setName(passedObject.getName());}

                    if(passedObject.getDateOfBirth().isEmpty()){
                        return new ResponseEntity<>("Date of birth be empty!", HttpStatus.NOT_ACCEPTABLE);
                    } else{localAccountHolder.setDateOfBirth(passedObject.getDateOfBirth());}

//                   if(passedObject.getPrimaryAddress().getPrimaryAddress().isEmpty()){
//                        return new ResponseEntity<>("Primary address cannot be empty!", HttpStatus.NOT_ACCEPTABLE);
//                    } else{localAccountHolder.setPrimaryAddress(passedObject.getPrimaryAddress());}
                    localAccountHolder.setPrimaryAddress(passedObject.getPrimaryAddress());
//                    if(passedObject.getPrimaryAddress().getMailingAddress().isEmpty()){
//                        return new ResponseEntity<>("Mailing address cannot be empty!", HttpStatus.NOT_ACCEPTABLE);
//                    } else{localAccountHolder.setMailingAddress(passedObject.getMailingAddress());}
                    localAccountHolder.setMailingAddress(passedObject.getMailingAddress());

                    localAccountHolder.setRole(verifiedRole);
                    accountHolderRepository.save(localAccountHolder);

                    break;
                    }
                case "THIRD_PARTY":{
                    ThirdParty localThirdParty =new ThirdParty();
                    localThirdParty.setUsername(passedObject.getUsername());
                    localThirdParty.setPassword(passedObject.getPassword());
                    localThirdParty.setRole(verifiedRole);

                    if(passedObject.getName().isEmpty()){
                        return new ResponseEntity<>("Username and password cannot be empty!", HttpStatus.NOT_ACCEPTABLE);
                    } else{localThirdParty.setName(passedObject.getName());}
                    thirdPartyRepository.save(localThirdParty);
                    break;
                }
            }
        }
        return new ResponseEntity<>("New ".concat(role).concat(" created"),
                HttpStatus.CREATED);
    }
}

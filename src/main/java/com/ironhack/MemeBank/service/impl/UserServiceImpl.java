package com.ironhack.MemeBank.service.impl;

import com.ironhack.MemeBank.dao.Role;
import com.ironhack.MemeBank.dao.users.AccountHolder;
import com.ironhack.MemeBank.dao.users.Admin;
import com.ironhack.MemeBank.dao.users.ThirdParty;
import com.ironhack.MemeBank.dao.users.User;
import com.ironhack.MemeBank.dto.CreateUserDTO;
import com.ironhack.MemeBank.enums.RoleType;
import com.ironhack.MemeBank.repository.*;
import com.ironhack.MemeBank.security.Passwords;
import com.ironhack.MemeBank.security.SecurityConfiguration;
import com.ironhack.MemeBank.service.interfaces.UserService;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    SecurityConfiguration securityConfiguration;

    public UserServiceImpl() {
    }

//    public void save(User user) {
//        userRepository.save(user);
//    }
//
//    public User findByUsername(String username) {
//        return userRepository.findByUsername(username).get();
//    }
//
//    public User saveUser (User user) {
//        return userRepository.save(user);
//    }
//
//    public List<User> findUserList() {
//        return userRepository.findAll();
//    }

        public ResponseEntity<?> storeAny(@RequestBody CreateUserDTO passedObject) {
        if(GenericValidator.isBlankOrNull(passedObject.getRoleType()) || GenericValidator.isBlankOrNull(String.valueOf(passedObject.getRole()))){
            return new ResponseEntity<>("RoleType must be specified!",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        //check if password or username are empty
        if(GenericValidator.isBlankOrNull(passedObject.getUsername()) || GenericValidator.isBlankOrNull(passedObject.getPassword())){
            return new ResponseEntity<>("Username and password cannot be empty!",
                    HttpStatus.NOT_ACCEPTABLE);
        }

        String role=(!GenericValidator.isBlankOrNull(passedObject.getRoleType())) ? passedObject.getRoleType().toUpperCase().replaceAll("\\s+", "") : passedObject.getRole().getName();
        Role verifiedRole;

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
            return new ResponseEntity<>("Role ".concat(role).concat(" does not exist."),
                    HttpStatus.NOT_ACCEPTABLE);
        }else{
            if (roleRepository.findByName(role).isPresent()){
                verifiedRole=roleRepository.findByName(role).get();
            }else{
                verifiedRole =new Role(role.toUpperCase());
                roleRepository.save(verifiedRole);
            }

            //create specific users

            switch (role) {
                case "ADMIN" -> {
                    Admin localAdmin = new Admin();
                    localAdmin.setUsername(passedObject.getUsername());
                    localAdmin.setPassword(securityConfiguration.passwordEncoder()
                            .encode(passedObject.getPassword()));
                    localAdmin.setRole(verifiedRole);
                    adminRepository.save(localAdmin);
                }
                case "ACCOUNT_HOLDER" -> {
                    AccountHolder localAccountHolder = new AccountHolder();
                    localAccountHolder.setUsername(passedObject.getUsername());
                    localAccountHolder.setPassword(securityConfiguration.passwordEncoder()
                            .encode(passedObject.getPassword()));

                    if (GenericValidator.isBlankOrNull(passedObject.getUsername())) {
                        return new ResponseEntity<>("Username and password cannot be empty!", HttpStatus.NOT_ACCEPTABLE);
                    } else {
                        localAccountHolder.setUsername(passedObject.getUsername());
                    }

                    if ((GenericValidator.isBlankOrNull(passedObject.getDateOfBirth()) || !GenericValidator.isDate(passedObject.getDateOfBirth(), "yyyy-MM-dd", true))

                    ) {
                        return new ResponseEntity<>("DateOfBirth cannot be empty and must be provided in yyyy-MM-dd format", HttpStatus.NOT_ACCEPTABLE);
                    }
//                    else if(GenericValidator.isBlankOrNull(passedObject.getLocalDateOfBirth().toString())){
//                        return new ResponseEntity<>("DateOfBirth cannot be empty and must be provided in yyyy-MM-dd format", HttpStatus.NOT_ACCEPTABLE);
//                    }
                    LocalDate dateOfBirth=(!GenericValidator.isBlankOrNull(passedObject.getDateOfBirth())) ? LocalDate.parse(passedObject.getDateOfBirth()) : LocalDate.now();
                    localAccountHolder.setDateOfBirth(dateOfBirth);
                    localAccountHolder.setPrimaryAddress(passedObject.getPrimaryAddress());
                    localAccountHolder.setMailingAddress(passedObject.getMailingAddress());
                    localAccountHolder.setRole(verifiedRole);
                    accountHolderRepository.save(localAccountHolder);
                }
                case "THIRD_PARTY" -> {
                    ThirdParty localThirdParty = new ThirdParty();
                    localThirdParty.setUsername(passedObject.getUsername());
                    localThirdParty.setPassword(securityConfiguration.passwordEncoder()
                            .encode(passedObject.getPassword()));
                    localThirdParty.setRole(verifiedRole);

                    var salt      = Passwords.getNextSalt();
                    var password  = localThirdParty.getPassword().toCharArray();
                    var secretKey = Passwords.hash(password, salt);
                    localThirdParty.setHashKey(Arrays.toString(secretKey));
                    localThirdParty.setSalt(Arrays.toString(salt));

                    if (GenericValidator.isBlankOrNull(passedObject.getUsername())) {
                        return new ResponseEntity<>("Username and password cannot be empty!", HttpStatus.NOT_ACCEPTABLE);
                    } else {
                        localThirdParty.setUsername(passedObject.getUsername());
                    }
                    thirdPartyRepository.save(localThirdParty);
                }
            }
        }
        return new ResponseEntity<>("New ".concat(role).concat(" created"),
                HttpStatus.CREATED);
    }

    public String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return  principal.toString();
        }
    }
}

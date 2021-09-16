package com.ironhack.MemeBank.service.impl;

import com.ironhack.MemeBank.dao.users.User;
import com.ironhack.MemeBank.repository.RoleRepository;
import com.ironhack.MemeBank.repository.UserRepository;
import com.ironhack.MemeBank.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


//    @Autowired
//    private BCryptPasswordEncoder passwordEncoder;

//    @Autowired
//    private AccountService accountService;

    public void save(User user) {
        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).get();
    }

//    public User findByEmail(String email) {
//        return userRepository.findByEmail(email);
//    }


//    public Optional<User> createUser(User user, Set<UserRoles> userRoles) {
//        Optional<User> localUser = userRepository.findByUsername(user.getUsername());
//
//        if (localUser.isPresent()) {
//            LOG.info("User with username {} already exist. Nothing will be done. ", user.getUsername());
//        } else {
////            String encryptedPassword = passwordEncoder.encode(user.getPassword());
////            user.setPassword(encryptedPassword);
//
//            for (UserRoles ur : userRoles) {
//                roleRepository.save(ur.getRole());
//            }
//
//            user.getUserRoles().addAll(userRoles);
//
////            user.setPrimaryAccount(accountService.createPrimaryAccount());
////            user.setSavingsAccount(accountService.createSavingsAccount());
//            userRepository.save(user);
//            localUser = userRepository.findByUsername(user.getUsername());
//        }
//
//        return localUser;
//    }

//    public boolean checkUserExists(String username, String email){
//        if (checkUsernameExists(username) || checkEmailExists(username)) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    public boolean checkUsernameExists(String username) {
        if (null != findByUsername(username)) {
            return true;
        }

        return false;
    }

//    public boolean checkEmailExists(String email) {
//        if (null != findByEmail(email)) {
//            return true;
//        }
//
//        return false;
//    }

    public User saveUser (User user) {
        return userRepository.save(user);
    }

    public List<User> findUserList() {
        return userRepository.findAll();
    }

//    public void enableUser (String username) {
//        User user = findByUsername(username);
//        user.setEnabled(true);
//        userDao.save(user);
//    }

//    public void disableUser (String username) {
//        User user = findByUsername(username);
//        user.setEnabled(false);
//        System.out.println(user.isEnabled());
//        userDao.save(user);
//        System.out.println(username + " is disabled.");
//    }
}

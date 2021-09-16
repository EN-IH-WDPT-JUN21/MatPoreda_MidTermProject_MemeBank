package com.ironhack.MemeBank.service.interfaces;

import com.ironhack.MemeBank.dao.users.User;
import com.ironhack.MemeBank.security.UserRoles;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
@Service
public interface UserService {
    User findByUsername(String username);

//    User findByEmail(String email);

//    boolean checkUserExists(String username, String email);
//
//    boolean checkUsernameExists(String username);

//    boolean checkEmailExists(String email);

    void save (User user);

//    Optional<User> createUser(User user, Set<UserRoles> userRoles);

    User saveUser (User user);

    List<User> findUserList();
//
//    void enableUser (String username);
//
//    void disableUser (String username);
}


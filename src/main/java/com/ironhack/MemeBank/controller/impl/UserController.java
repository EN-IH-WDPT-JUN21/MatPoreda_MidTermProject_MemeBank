package com.ironhack.MemeBank.controller.impl;

import com.ironhack.MemeBank.dao.users.User;
import com.ironhack.MemeBank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User store(@RequestBody @Valid User user) {
        return userRepository.save(user);
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}

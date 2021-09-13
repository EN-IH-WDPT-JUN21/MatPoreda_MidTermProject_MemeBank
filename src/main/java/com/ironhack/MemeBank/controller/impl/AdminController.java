package com.ironhack.MemeBank.controller.impl;

import com.ironhack.MemeBank.dao.users.Admin;
import com.ironhack.MemeBank.dao.users.User;
import com.ironhack.MemeBank.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class AdminController {
    @Autowired
    AdminRepository adminRepository;

    @PostMapping("/admins")
    @ResponseStatus(HttpStatus.CREATED)
    public Admin store(@RequestBody @Valid Admin admin) {
        return adminRepository.save(admin);
    }

    @GetMapping("/admins")
    @ResponseStatus(HttpStatus.OK)
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }
}

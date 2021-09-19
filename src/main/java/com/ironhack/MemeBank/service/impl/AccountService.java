package com.ironhack.MemeBank.service.impl;

import com.ironhack.MemeBank.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;

@Service
public class AccountService {
    @Autowired
    AccountRepository accountRepository;

}

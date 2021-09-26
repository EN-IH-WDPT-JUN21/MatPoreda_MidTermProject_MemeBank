package com.ironhack.MemeBank.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.MemeBank.dao.Money;
import com.ironhack.MemeBank.dao.Role;
import com.ironhack.MemeBank.dao.accounts.Account;
import com.ironhack.MemeBank.dao.accounts.Savings;
import com.ironhack.MemeBank.dao.users.AccountHolder;
import com.ironhack.MemeBank.dao.users.Admin;
import com.ironhack.MemeBank.dao.users.ThirdParty;
import com.ironhack.MemeBank.dao.users.User;
import com.ironhack.MemeBank.enums.AccountType;
import com.ironhack.MemeBank.enums.Status;
import com.ironhack.MemeBank.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserServiceImplTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    TransactionService transactionService;

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ThirdPartyRepository thirdPartyRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    AccountHolderRepository accountHolderRepository;


    @Autowired
    AccountService accountService;

    @Autowired
    SavingsRepository savingsRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper=new ObjectMapper().findAndRegisterModules();


    Admin admin1;
    Admin admin2;

    AccountHolder accountHolder1;
    AccountHolder accountHolder2;

    ThirdParty thirdParty1;
    ThirdParty thirdParty2;

    Savings savings1;
    Savings savings2;

    Role roleAdmin;
    Role roleThirdParty;
    Role roleAccountHolder;

    User user1;
    User user2;
    Account account1;
    Account account2;
    @BeforeEach
    void setUp() {
        mockMvc= MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        roleAdmin=new Role("ADMIN");
        admin1=new Admin();
        admin1.setRole(roleAdmin);
        admin1.setUsername("admin99_name");
        admin1.setPassword("admin_password");
        adminRepository.save(admin1);

        roleAccountHolder=new Role("ACCOUNT_HOLDER");
        accountHolder1=new AccountHolder();
        accountHolder1.setRole(roleAccountHolder);
        accountHolder1.setUsername("accountHolder99_name");
        accountHolder1.setPassword("accountHolder1_password");

        roleThirdParty=new Role("THIRD_PARTY");
        thirdParty1=new ThirdParty();
        thirdParty1.setRole(roleThirdParty);
        thirdParty1.setUsername("thirdParty99_name");
        thirdParty1.setPassword("thirdParty1_password");
        thirdPartyRepository.save(thirdParty1);

        savings1=new Savings();
        savings1.setAccountType(AccountType.SAVINGS);
        savings1.setPrimaryOwner(accountHolder1);
        savings1.setStatus(Status.ACTIVE);
        savings1.setBalance(new Money(new BigDecimal("1000")));
        savings1.setPenaltyFee(new Money(new BigDecimal("40")));
        savings1.setCreationDate(LocalDate.of(2020,12,1));
        savings1.setSecretKey("[B@25ecdecd");

        savings2=new Savings();
        savings2.setAccountType(AccountType.SAVINGS);
        savings2.setPrimaryOwner(accountHolder1);
        savings2.setStatus(Status.ACTIVE);
        savings2.setBalance(new Money(new BigDecimal("1000")));
        savings2.setPenaltyFee(new Money(new BigDecimal("40")));
        savings2.setCreationDate(LocalDate.of(2020,12,1));
        savings2.setSecretKey("[C@25ecdecd");
        savingsRepository.saveAll(List.of(savings1,savings2));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        adminRepository.deleteAll();
        thirdPartyRepository.deleteAll();
        accountHolderRepository.deleteAll();

        transactionRepository.deleteAll();

        accountRepository.deleteAll();
        savingsRepository.deleteAll();

    }


    @Test
    void getCurrentUsername() {
    }
}
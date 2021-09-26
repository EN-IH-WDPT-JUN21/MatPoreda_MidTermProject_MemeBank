package com.ironhack.MemeBank.controller.impl;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.MemeBank.dao.Money;
import com.ironhack.MemeBank.dao.Role;
import com.ironhack.MemeBank.dao.Transaction;
import com.ironhack.MemeBank.dao.accounts.Account;
import com.ironhack.MemeBank.dao.accounts.Savings;
import com.ironhack.MemeBank.dao.users.AccountHolder;
import com.ironhack.MemeBank.dao.users.Admin;
import com.ironhack.MemeBank.dao.users.ThirdParty;
import com.ironhack.MemeBank.dao.users.User;
import com.ironhack.MemeBank.dto.CreateAccountDTO;
import com.ironhack.MemeBank.dto.TransactionDTO;
import com.ironhack.MemeBank.enums.*;
import com.ironhack.MemeBank.repository.*;
import com.ironhack.MemeBank.service.impl.AccountService;
import com.ironhack.MemeBank.service.impl.TransactionService;
import com.ironhack.MemeBank.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TransactionControllerTest {
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
    SavingsRepository savingsRepository;

    @Autowired
    AccountService accountService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper=new ObjectMapper();

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

    Transaction transaction1;
    Transaction transaction2;

    @BeforeEach
    void setUp() {
        mockMvc= MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        roleAdmin=new Role("ADMIN");
        admin1=new Admin();
        admin1.setRole(roleAdmin);
        admin1.setUsername("admin_name");
        admin1.setPassword("admin_password");
        adminRepository.save(admin1);

        roleAccountHolder=new Role("ACCOUNT_HOLDER");
        accountHolder1=new AccountHolder();
        accountHolder1.setRole(roleAccountHolder);
        accountHolder1.setUsername("accountHolder1_name");
        accountHolder1.setPassword("accountHolder1_password");

        roleThirdParty=new Role("THIRD_PARTY");
        thirdParty1=new ThirdParty();
        thirdParty1.setRole(roleThirdParty);
        thirdParty1.setUsername("thirdParty1_name");
        thirdParty1.setPassword("thirdParty1_password");
        thirdParty1.setHashKey("[B@25ecdecd");
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


        transaction1=new Transaction();
        transaction1.setTransactionInitiator(accountHolder1);
        transaction1.setAccount(savings1);
        transaction1.setDescription("Test transaction 1");
        transaction1.setAmount(new Money(new BigDecimal("100")));
        transaction1.setStatus(TransactionStatus.ACCEPTED);
        transaction1.setType(TransactionType.TRANSFER);
        transaction1.setDate(LocalDateTime.now());

        transaction2=new Transaction();
        transaction2.setTransactionInitiator(accountHolder1);
        transaction2.setAccount(savings2);
        transaction2.setDescription("Test transaction 2");
        transaction2.setAmount(new Money(new BigDecimal("100")));
        transaction2.setStatus(TransactionStatus.ACCEPTED);
        transaction2.setType(TransactionType.TRANSFER);
        transaction2.setDate(LocalDateTime.now());

        savings1.setTransactionList(Set.of(transaction1, transaction2));
        savingsRepository.saveAll(List.of(savings1,savings2));

    }

    @AfterEach
    void tearDown() {
        adminRepository.deleteAll();
        userRepository.deleteAll();
        thirdPartyRepository.deleteAll();
        accountHolderRepository.deleteAll();
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void getUserTransactions() throws Exception{

    }

    @Test
    void getAllTransactions() throws Exception{
        MvcResult mvcResult = mockMvc.perform(
                get("/transactions/all").with(user("admin_name").password("admin_password").roles("ADMIN")))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Test transaction 1"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Test transaction 2"));
    }

    @Test
    void storeThirdPartyTransaction() throws Exception{
        TransactionDTO transaction3 =new TransactionDTO();

        Long           id            =savingsRepository.findBySecretKey("[C@25ecdecd").get().getId();

        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
        String body=objectMapper.writeValueAsString(transaction3);
        System.out.println(body);
        MvcResult mvcResult=mockMvc.perform(post("//transactions/third_party").with(user("thirdParty1").password("thirdParty1").roles("THIRD_PARTY"))
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("balance successfully changed"));

    }

    @Test
    void storeAccountHolderTransaction() {
    }
}
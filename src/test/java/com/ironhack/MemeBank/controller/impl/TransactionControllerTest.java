package com.ironhack.MemeBank.controller.impl;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.MemeBank.ApplicationTest;
import com.ironhack.MemeBank.dao.Money;
import com.ironhack.MemeBank.dao.Role;
import com.ironhack.MemeBank.dao.Transaction;
import com.ironhack.MemeBank.dao.accounts.Savings;
import com.ironhack.MemeBank.dao.users.AccountHolder;
import com.ironhack.MemeBank.dao.users.Admin;
import com.ironhack.MemeBank.dao.users.User;
import com.ironhack.MemeBank.enums.*;
import com.ironhack.MemeBank.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApplicationTest.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class TransactionControllerTest {
    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    TransactionRepository transactionRepository;

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


    @Mock
    User principal;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper=new ObjectMapper();

    Admin admin1;

    AccountHolder accountHolder1;

    Savings savings1;
    Savings savings2;

    Role roleAdmin;
    Role roleAccountHolder;

    Transaction transaction1;
    Transaction transaction2;

    @BeforeEach
    void setUp() {
        User applicationUser = mock(User.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.setContext(securityContext);

        MockitoAnnotations.initMocks(this);
        mockMvc= MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        roleAdmin=new Role("ADMIN");
        admin1=new Admin();
        admin1.setRole(roleAdmin);
        admin1.setUsername("admin_name2");
        admin1.setPassword("admin_password");
        adminRepository.save(admin1);

        roleAccountHolder=new Role("ACCOUNT_HOLDER");
        accountHolder1=new AccountHolder();
        accountHolder1.setRole(roleAccountHolder);
        accountHolder1.setUsername("accountHolder2_name");
        accountHolder1.setPassword("accountHolder_password");

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
    void getAllTransactions() throws Exception{
        MvcResult mvcResult = mockMvc.perform(
                get("/transactions/all").with(user("admin_name2").password("admin_password").roles("ADMIN")))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Test transaction 1"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Test transaction 2"));
    }

}
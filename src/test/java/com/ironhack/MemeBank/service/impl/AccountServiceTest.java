package com.ironhack.MemeBank.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.MemeBank.ApplicationTest;
import com.ironhack.MemeBank.dao.Money;
import com.ironhack.MemeBank.dao.Role;
import com.ironhack.MemeBank.dao.Transaction;
import com.ironhack.MemeBank.dao.accounts.CreditCard;
import com.ironhack.MemeBank.dao.accounts.Savings;
import com.ironhack.MemeBank.dao.users.AccountHolder;
import com.ironhack.MemeBank.dto.CreateAccountDTO;
import com.ironhack.MemeBank.enums.AccountType;
import com.ironhack.MemeBank.enums.Status;
import com.ironhack.MemeBank.enums.TransactionStatus;
import com.ironhack.MemeBank.enums.TransactionType;
import com.ironhack.MemeBank.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(classes = ApplicationTest.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AccountServiceTest {


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
    CheckingRepository checkingRepository;

    @Autowired
    StudentCheckingRepository studentCheckingRepository;

    @Autowired
    CreditCardRepository creditCardRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    SavingsRepository savingsRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper=new ObjectMapper().findAndRegisterModules();

    AccountHolder accountHolder1;
    Role roleAccountHolder;

    @BeforeEach
    void setUp() {
        mockMvc= MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Authentication a = SecurityContextHolder.getContext().getAuthentication();

        roleAccountHolder=new Role("ACCOUNT_HOLDER");
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        adminRepository.deleteAll();
        thirdPartyRepository.deleteAll();
        accountHolderRepository.deleteAll();
        creditCardRepository.deleteAll();
        transactionRepository.deleteAll();
        studentCheckingRepository.deleteAll();
        accountRepository.deleteAll();
        savingsRepository.deleteAll();
        checkingRepository.deleteAll();

    }

    @Test
    void checkLastPenaltyFee() throws Exception{
        roleAccountHolder=new Role("ACCOUNT_HOLDER");
        accountHolder1=new AccountHolder();
        accountHolder1.setRole(roleAccountHolder);
        accountHolder1.setUsername("accountHolder1_name_account_service");
        accountHolder1.setPassword("accountHolder_password");

        Savings savings1 =new Savings();
        savings1.setAccountType(AccountType.SAVINGS);
        savings1.setPrimaryOwner(accountHolder1);
        savings1.setStatus(Status.ACTIVE);
        savings1.setBalance(new Money(new BigDecimal("1000")));
        savings1.setPenaltyFee(new Money(new BigDecimal("40")));
        savings1.setCreationDate(LocalDate.of(2020,12,1));
        savings1.setSecretKey("[B@25ecdecd");

        Transaction transaction1=new Transaction();
        transaction1.setTransactionInitiator(accountHolder1);
        transaction1.setAccount(savings1);
        transaction1.setDescription("Test transaction 1");
        transaction1.setAmount(new Money(new BigDecimal("100")));
        transaction1.setStatus(TransactionStatus.ACCEPTED);
        transaction1.setType(TransactionType.TRANSFER);
        transaction1.setDate(LocalDateTime.now());

        assertFalse(accountService.checkLastPenaltyFee(savings1,transaction1));
        Transaction penaltyFee=transactionService.newPenaltyFee(savings1);
        transactionRepository.save(penaltyFee);
        savings1=savingsRepository.save(savings1);
        assertTrue(accountService.checkLastPenaltyFee(savings1,transaction1));
    }

    @Test
    void findMinimumBalance() {
        roleAccountHolder=new Role("ACCOUNT_HOLDER");
        accountHolder1=new AccountHolder();
        accountHolder1.setRole(roleAccountHolder);
        accountHolder1.setUsername("accountHolder1_name_account_service");
        accountHolder1.setPassword("accountHolder_password");

        Savings savings1 =new Savings();
        savings1.setAccountType(AccountType.SAVINGS);
        savings1.setPrimaryOwner(accountHolder1);
        savings1.setStatus(Status.ACTIVE);
        savings1.setBalance(new Money(new BigDecimal("1000.00000000")));
        savings1.setPenaltyFee(new Money(new BigDecimal("40")));
        savings1.setCreationDate(LocalDate.of(2020,12,1));
        savings1.setSecretKey("[B@25ecdecd");
        savings1=savingsRepository.save(savings1);

        assertTrue(new BigDecimal("1000.00000000").compareTo(accountService.findMinimumBalance(savings1).getAmount())==0);

    }
}
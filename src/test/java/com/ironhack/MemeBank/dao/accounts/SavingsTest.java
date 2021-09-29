package com.ironhack.MemeBank.dao.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.MemeBank.dao.Money;
import com.ironhack.MemeBank.dao.Role;
import com.ironhack.MemeBank.dao.users.AccountHolder;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class SavingsTest {

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
    CheckingRepository checkingRepository;

    @Autowired
    StudentCheckingRepository studentCheckingRepository;

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

        transactionRepository.deleteAll();
        studentCheckingRepository.deleteAll();
        accountRepository.deleteAll();
        savingsRepository.deleteAll();
        checkingRepository.deleteAll();

    }

    @Test
    void testSavings() {
        roleAccountHolder=new Role("ACCOUNT_HOLDER");
        accountHolder1=new AccountHolder();
        accountHolder1.setRole(roleAccountHolder);
        accountHolder1.setUsername("accountHolder1_name");
        accountHolder1.setPassword("accountHolder_password");

        Savings savings1=new Savings();
        savings1.setAccountType(AccountType.SAVINGS);
        savings1.setPrimaryOwner(accountHolder1);
        savings1.setStatus(Status.ACTIVE);
        savings1.setBalance(new Money(new BigDecimal("1000")));
        savings1.setPenaltyFee(new Money(new BigDecimal("40")));
        savings1.setCreationDate(LocalDate.of(2020,12,1));
        savings1.setSecretKey("[B@25ecdecd");
        savings1=savingsRepository.save(savings1);
        assertEquals(new BigDecimal("1000.0000000000"), savingsRepository.findById(savings1.getId()).get().getBalance().getAmount());
        assertEquals(new BigDecimal("40.0000000000"), savingsRepository.findById(savings1.getId()).get().getPenaltyFee().getAmount());
        assertEquals(LocalDate.of(2020,12,1), savingsRepository.findById(savings1.getId()).get().getCreationDate());
        assertEquals("[B@25ecdecd", savingsRepository.findById(savings1.getId()).get().getSecretKey());
        assertEquals(AccountType.SAVINGS, savingsRepository.findById(savings1.getId()).get().getAccountType());
        assertEquals(new BigDecimal("0.00250000"), savingsRepository.findById(savings1.getId()).get().getInterestRate());
        assertEquals(new BigDecimal("1000.00000000"), savingsRepository.findById(savings1.getId()).get().getMinimumBalance().getAmount());
        assertNull(savingsRepository.findById(savings1.getId()).get().getMonthlyMaintenanceFee());
    }

}
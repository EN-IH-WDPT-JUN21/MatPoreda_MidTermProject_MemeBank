package com.ironhack.MemeBank.dao.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.MemeBank.ApplicationTest;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = ApplicationTest.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CheckingTest {


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
        checkingRepository.deleteAll();
        accountRepository.deleteAll();
        savingsRepository.deleteAll();
        checkingRepository.deleteAll();

    }

    @Test
    void testChecking() {
        roleAccountHolder=new Role("ACCOUNT_HOLDER");
        accountHolder1=new AccountHolder();
        accountHolder1.setRole(roleAccountHolder);
        accountHolder1.setUsername("accountHolder1_name_checking");
        accountHolder1.setPassword("accountHolder_password");

        Checking checking=new Checking();
        checking.setAccountType(AccountType.CHECKING);
        checking.setPrimaryOwner(accountHolder1);
        checking.setStatus(Status.ACTIVE);
        checking.setBalance(new Money(new BigDecimal("1000")));
        checking.setPenaltyFee(new Money(new BigDecimal("40")));
        checking.setCreationDate(LocalDate.of(2020,12,1));
        checking.setSecretKey("[B@25ecdecd");
        checking.setBalance(new Money(new BigDecimal("1000")));

        Checking test=checkingRepository.save(checking);
        assertEquals(new BigDecimal("1000.0000000000"), checkingRepository.findById(test.getId()).get().getBalance().getAmount());
        assertTrue(checkingRepository.findById(test.getId()).get().getMonthlyMaintenanceFee().getAmount().compareTo(new BigDecimal("0"))==0);
        assertTrue(new BigDecimal("0").compareTo(checkingRepository.findById(test.getId()).get().getMinimumBalance().getAmount())==0);
        assertEquals(new BigDecimal("40.0000000000"), checkingRepository.findById(test.getId()).get().getPenaltyFee().getAmount());
    }

}
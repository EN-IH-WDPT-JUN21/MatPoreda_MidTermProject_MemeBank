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
class StudentCheckingTest {



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
    void getMonthlyMaintenanceFee() {
        roleAccountHolder=new Role("ACCOUNT_HOLDER");
        accountHolder1=new AccountHolder();
        accountHolder1.setRole(roleAccountHolder);
        accountHolder1.setUsername("accountHolder1_name_student_checking");
        accountHolder1.setPassword("accountHolder_password");

        StudentChecking studentChecking=new StudentChecking();
        studentChecking.setAccountType(AccountType.STUDENT_CHECKING);
        studentChecking.setPrimaryOwner(accountHolder1);
        studentChecking.setStatus(Status.ACTIVE);
        studentChecking.setBalance(new Money(new BigDecimal("1000")));
        studentChecking.setPenaltyFee(new Money(new BigDecimal("40")));
        studentChecking.setCreationDate(LocalDate.of(2020,12,1));
        studentChecking.setSecretKey("[B@25ecdecd");
        studentChecking.setBalance(new Money(new BigDecimal("1000")));

        StudentChecking test=studentCheckingRepository.save(studentChecking);
        assertEquals(new BigDecimal("1000.0000000000"), studentCheckingRepository.findById(test.getId()).get().getBalance().getAmount());
        assertNull(studentCheckingRepository.findById(test.getId()).get().getMonthlyMaintenanceFee());
    }
}
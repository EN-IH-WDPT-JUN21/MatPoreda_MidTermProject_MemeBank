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
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = ApplicationTest.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CreditCardTest {

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
    CreditCardRepository creditCardRepository;

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
    void testCreditCard() {
        roleAccountHolder=new Role("ACCOUNT_HOLDER");
        accountHolder1=new AccountHolder();
        accountHolder1.setRole(roleAccountHolder);
        accountHolder1.setUsername("accountHolder1_name");
        accountHolder1.setPassword("accountHolder_password");

        CreditCard creditCard1=new CreditCard();
        creditCard1.setAccountType(AccountType.CREDIT_CARD);
        creditCard1.setPrimaryOwner(accountHolder1);
        creditCard1.setStatus(Status.ACTIVE);
        creditCard1.setBalance(new Money(new BigDecimal("100.0000000000")));
        creditCard1.setPenaltyFee(new Money(new BigDecimal("40")));
        creditCard1.setCreationDate(LocalDate.of(2020,12,1));
        creditCard1.setSecretKey("[B@25ecdecd");
        creditCard1=creditCardRepository.save(creditCard1);
        assertEquals(new BigDecimal("100.0000000000"), creditCardRepository.findById(creditCard1.getId()).get().getBalance().getAmount());
        assertEquals(new BigDecimal("40.0000000000"), creditCardRepository.findById(creditCard1.getId()).get().getPenaltyFee().getAmount());
        assertEquals(LocalDate.of(2020,12,1), creditCardRepository.findById(creditCard1.getId()).get().getCreationDate());
        assertEquals("[B@25ecdecd", creditCardRepository.findById(creditCard1.getId()).get().getSecretKey());
        assertEquals(AccountType.CREDIT_CARD, creditCardRepository.findById(creditCard1.getId()).get().getAccountType());
        assertEquals(new BigDecimal("0.2000000000"), creditCardRepository.findById(creditCard1.getId()).get().getInterestRate());
        assertEquals(new BigDecimal("100.0000000000"), creditCardRepository.findById(creditCard1.getId()).get().getCreditLimit().getAmount());
        assertNull(creditCardRepository.findById(creditCard1.getId()).get().getMonthlyMaintenanceFee());
    }

}
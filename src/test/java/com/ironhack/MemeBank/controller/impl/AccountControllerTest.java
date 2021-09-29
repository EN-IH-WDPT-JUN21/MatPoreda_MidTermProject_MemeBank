package com.ironhack.MemeBank.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.MemeBank.dao.Money;
import com.ironhack.MemeBank.dao.Role;
import com.ironhack.MemeBank.dao.accounts.Savings;
import com.ironhack.MemeBank.dao.users.AccountHolder;
import com.ironhack.MemeBank.dao.users.Admin;
import com.ironhack.MemeBank.dao.users.ThirdParty;
import com.ironhack.MemeBank.dto.CreateAccountDTO;
import com.ironhack.MemeBank.dto.TransactionDTO;
import com.ironhack.MemeBank.enums.AccountType;
import com.ironhack.MemeBank.enums.Status;
import com.ironhack.MemeBank.repository.*;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AccountControllerTest {

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

    Admin admin1;
    AccountHolder accountHolder1;
    ThirdParty thirdParty1;
    Savings savings1;
    Role roleAdmin;
    Role roleThirdParty;
    Role roleAccountHolder;

    @BeforeEach
    void setUp() {
        mockMvc= MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        roleAdmin=new Role("ADMIN");
        admin1=new Admin();
        admin1.setRole(roleAdmin);
        admin1.setUsername("admin_name1");
        admin1.setPassword("admin_password");
        adminRepository.save(admin1);

        roleAccountHolder=new Role("ACCOUNT_HOLDER");
        accountHolder1=new AccountHolder();
        accountHolder1.setRole(roleAccountHolder);
        accountHolder1.setUsername("accountHolder1_name");
        accountHolder1.setPassword("accountHolder_password");

        roleThirdParty=new Role("THIRD_PARTY");
        thirdParty1=new ThirdParty();
        thirdParty1.setRole(roleThirdParty);
        thirdParty1.setUsername("thirdParty1_name");
        thirdParty1.setPassword("thirdParty_password");
        thirdPartyRepository.save(thirdParty1);

        savings1=new Savings();
        savings1.setAccountType(AccountType.SAVINGS);
        savings1.setPrimaryOwner(accountHolder1);
        savings1.setStatus(Status.ACTIVE);
        savings1.setBalance(new Money(new BigDecimal("1000")));
        savings1.setPenaltyFee(new Money(new BigDecimal("40")));
        savings1.setCreationDate(LocalDate.of(2020,12,1));
        savings1.setSecretKey("[B@25ecdecd");
        savings1=savingsRepository.save(savings1);
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
        checkingRepository.deleteAll();
    }


    @Test
    void getAllAccountsWithAdminAuthorities() throws Exception{
        MvcResult mvcResult = mockMvc.perform(
                get("/accounts/all").with(user("admin_name1").password("admin_password").roles("ADMIN")))
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("accountHolder1_name"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("[B@25ecdecd"));
    }

    @Test
    void store() throws Exception{
        CreateAccountDTO credit_card =new CreateAccountDTO();
        credit_card.setAccountType("CREDIT_CARD");
        credit_card.setPrimaryOwnerName(accountHolder1.getUsername());
        credit_card.setBalance("1000");
        credit_card.setPenaltyFee("40");
        credit_card.setInterestRate("0.10");
        credit_card.setCreationDate("2020-12-10");

        String body=objectMapper.writeValueAsString(credit_card);
        System.out.println(body);
        MvcResult mvcResult=mockMvc.perform(post("/accounts").with(user("admin_name1").password("admin_password").roles("ADMIN"))
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("New credit card account created"));
        assertTrue(accountRepository.findByPrimaryOwnerOrSecondaryOwner(accountHolder1, accountHolder1).size()==2);
    }

    @Test
    void setBalanceSuccess() throws Exception{
        TransactionDTO setBalance=new TransactionDTO();
        Long id=savings1.getId();
        setBalance.setBalance("1000");

        String body=objectMapper.writeValueAsString(setBalance);
        System.out.println(body);
        MvcResult mvcResult=mockMvc.perform(post("/accounts/set_balance/"+id.toString()).with(user("admin_name1").password("admin_password").roles("ADMIN"))
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("balance successfully changed"));
    }

    @Test
    void setBalanceNotValidBalance() throws Exception{
        TransactionDTO setBalance=new TransactionDTO();
        Long id=savings1.getId();
        setBalance.setBalance("T1000");

        String body=objectMapper.writeValueAsString(setBalance);
        System.out.println(body);
        MvcResult mvcResult=mockMvc.perform(post("/accounts/set_balance/"+id.toString()).with(user("admin_name1").password("admin_password").roles("ADMIN"))
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable())
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Balance must be provided as a valid double"));
    }
}
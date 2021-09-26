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
import com.ironhack.MemeBank.dto.CreateUserDTO;
import com.ironhack.MemeBank.enums.AccountType;
import com.ironhack.MemeBank.enums.RoleType;
import com.ironhack.MemeBank.enums.Status;
import com.ironhack.MemeBank.repository.*;
import com.ironhack.MemeBank.service.impl.AccountService;
import com.ironhack.MemeBank.service.impl.TransactionService;
import com.ironhack.MemeBank.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
        adminRepository.deleteAll();
        userRepository.deleteAll();
        thirdPartyRepository.deleteAll();
        accountHolderRepository.deleteAll();
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
    }


    @Test
    void getAllAccountsWithAdminAuthorities() throws Exception{
        MvcResult mvcResult = mockMvc.perform(
                get("/accounts/all").with(user("admin_name").password("admin_password").roles("ADMIN")))
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("accountHolder1_name"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("[C@25ecdecd"));
    }

    @Test
    void getNoAccountsWithAccountHolderAuthorities() throws Exception{
        Authentication authentication = Mockito.mock(Authentication.class);
// Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        MvcResult mvcResult = mockMvc.perform(
                get("/accounts/all").with(user("accountHolder1_name").password("accountHolder1_password").roles("ACCOUNT_HOLDER")))
 //               .andExpect(status().isOk())
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        assertFalse(mvcResult.getResponse().getContentAsString().contains("accountHolder1_name"));
        assertFalse(mvcResult.getResponse().getContentAsString().contains("[C@25ecdecd"));
    }

    @Test
    void getOwnedAccountsWithUserAuthorities() throws Exception{
        Authentication authentication = Mockito.mock(Authentication.class);
// Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        MvcResult mvcResult = mockMvc.perform(
                get("/accounts").with(user("accountHolder1_name").password("accountHolder1_password").roles("ACCOUNT_HOLDER")))
 //               .andExpect(status().isOk())
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("accountHolder1_name"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("[C@25ecdecd"));
    }

    @Test
    void getNoAccountsWithThirdPartyAuthorities() throws Exception{
        Authentication authentication = Mockito.mock(Authentication.class);
// Mockito.whens() for your authorization object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        MvcResult mvcResult = mockMvc.perform(
                get("/accounts").with(user("thirdParty1_name").password("thirdParty1_password").roles("THIRD_PARTY")))
 //               .andExpect(status().isOk())
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("accountHolder1_name"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("[C@25ecdecd"));
    }

    @Test
    void store() throws Exception{
        CreateAccountDTO savings3 =new CreateAccountDTO();
        savings3.setAccountType("SAVINGS");
        savings3.setPrimaryOwnerName(accountHolder1.getUsername());
        savings3.setBalance("1000");
        savings3.setPenaltyFee("40");
        savings3.setCreationDate("2020-12-10");

        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
        String body=objectMapper.writeValueAsString(savings3);
        System.out.println(body);
        MvcResult mvcResult=mockMvc.perform(post("/accounts").with(user("accountHolder1_name").password("accountHolder1_password").roles("ACCOUNT_HOLDER"))
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated())
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("New savings account created"));
        assertTrue(accountRepository.findByPrimaryOwnerOrSecondaryOwner(accountHolder1, accountHolder1).size()==3);
    }

    @Test
    void setBalance() throws Exception{
        CreateAccountDTO savings3 =new CreateAccountDTO();
        Long id=savingsRepository.findBySecretKey("[C@25ecdecd").get().getId();
        savings3.setBalance("5600.00");

        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
        String body=objectMapper.writeValueAsString(savings3);
        System.out.println(body);
        MvcResult mvcResult=mockMvc.perform(post("/accounts/set_balance/"+id).with(user("admin_name").password("admin_password").roles("ADMIN"))
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("balance successfully changed"));
    }
}
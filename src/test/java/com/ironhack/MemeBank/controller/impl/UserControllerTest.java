package com.ironhack.MemeBank.controller.impl;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.MemeBank.dao.Role;
import com.ironhack.MemeBank.dao.accounts.Account;
import com.ironhack.MemeBank.dao.accounts.Savings;
import com.ironhack.MemeBank.dao.users.AccountHolder;
import com.ironhack.MemeBank.dao.users.Admin;
import com.ironhack.MemeBank.dao.users.ThirdParty;
import com.ironhack.MemeBank.dao.users.User;
import com.ironhack.MemeBank.dto.CreateUserDTO;
import com.ironhack.MemeBank.enums.RoleType;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UserControllerTest {

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
        accountHolderRepository.save(accountHolder1);

        roleThirdParty=new Role("THIRD_PARTY");
        thirdParty1=new ThirdParty();
        thirdParty1.setRole(roleThirdParty);
        thirdParty1.setUsername("thirdParty1_name");
        thirdParty1.setPassword("thirdParty1_password");
        thirdPartyRepository.save(thirdParty1);
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
    void getAllUsers() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/users")).andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("admin_name"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("accountHolder1_name"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("thirdParty1_name"));
    }

    @Test
    void getAdmins() throws Exception{
        MvcResult mvcResult = mockMvc.perform(get("/users/admins")).andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("admin_name"));
        assertFalse(mvcResult.getResponse().getContentAsString().contains("accountHolder1_name"));
        assertFalse(mvcResult.getResponse().getContentAsString().contains("thirdParty1_name"));
    }

    @Test
    void getThirdParty() throws Exception{
        MvcResult mvcResult = mockMvc.perform(get("/users/third_party")).andExpect(status().isOk()).andReturn();
        assertFalse(mvcResult.getResponse().getContentAsString().contains("admin_name"));
        assertFalse(mvcResult.getResponse().getContentAsString().contains("accountHolder1_name"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("thirdParty1_name"));
    }

    @Test
    void getAccountHolders() throws Exception{
        MvcResult mvcResult = mockMvc.perform(get("/users/account_holders")).andExpect(status().isOk()).andReturn();
        assertFalse(mvcResult.getResponse().getContentAsString().contains("admin_name"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("accountHolder1_name"));
        assertFalse(mvcResult.getResponse().getContentAsString().contains("thirdParty1_name"));
    }

    @Test
    void storeNewAdmin() throws Exception{
        Admin admin2=new Admin();
            admin2.setRole(roleAdmin);
            admin2.setUsername("admin2_name");
            admin2.setPassword("admin2_password");
        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
        String body=objectMapper.writeValueAsString(admin2);
        System.out.println(body);
        MvcResult mvcResult=mockMvc.perform(post("/users")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("New ADMIN created"));
        assertTrue(userRepository.findByUsername("admin2_name").isPresent());
        assertEquals(RoleType.ADMIN.toString(), userRepository.findByUsername("admin2_name").get().getRole().getName());
    }

    @Test
    void storeNewAccountHolder() throws Exception{
        CreateUserDTO accountHolder2 =new CreateUserDTO();
        accountHolder2.setRole(roleAccountHolder);
        accountHolder2.setUsername("accountHolder2_name");
        accountHolder2.setPassword("accountHolder2_password");
        accountHolder2.setDateOfBirth("1980-12-31");

        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
        String body=objectMapper.writeValueAsString(accountHolder2);
        System.out.println(body);
        MvcResult mvcResult=mockMvc.perform(post("/users")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("New ACCOUNT_HOLDER created"));
        assertTrue(userRepository.findByUsername("accountHolder2_name").isPresent());
        assertEquals(RoleType.ACCOUNT_HOLDER.toString(), userRepository.findByUsername("accountHolder2_name").get().getRole().getName());
    }

    @Test
    void storeNewThirdParty() throws Exception{
        CreateUserDTO thirdParty2 =new CreateUserDTO();
        thirdParty2.setRole(roleThirdParty);
        thirdParty2.setUsername("thirdParty2_name");
        thirdParty2.setPassword("thirdParty2_password");
        thirdParty2.setDateOfBirth("1980-12-31");

        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
        String body=objectMapper.writeValueAsString(thirdParty2);
        System.out.println(body);
        MvcResult mvcResult=mockMvc.perform(post("/users")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("New THIRD_PARTY created"));
        assertTrue(userRepository.findByUsername("thirdParty2_name").isPresent());
        assertEquals(RoleType.THIRD_PARTY.toString(), userRepository.findByUsername("thirdParty2_name").get().getRole().getName());
    }
}
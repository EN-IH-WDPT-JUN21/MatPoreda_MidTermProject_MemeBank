package com.ironhack.MemeBank.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.MemeBank.dao.Address;
import com.ironhack.MemeBank.dao.Role;
import com.ironhack.MemeBank.dao.users.AccountHolder;
import com.ironhack.MemeBank.dao.users.Admin;
import com.ironhack.MemeBank.dao.users.ThirdParty;
import com.ironhack.MemeBank.dto.CreateUserDTO;
import com.ironhack.MemeBank.enums.RoleType;
import com.ironhack.MemeBank.repository.*;
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
    UserRepository userRepository;

    @Autowired
    ThirdPartyRepository thirdPartyRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    AccountHolderRepository accountHolderRepository;


    private MockMvc mockMvc;
    private final ObjectMapper objectMapper=new ObjectMapper().findAndRegisterModules();


    Admin admin1;

    AccountHolder accountHolder1;

    ThirdParty thirdParty3;

    Role roleAdmin;
    Role roleThirdParty;
    Role roleAccountHolder;


    @BeforeEach
    void setUp() {
        mockMvc= MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        roleAdmin=new Role("ADMIN");
        admin1=new Admin();
        admin1.setRole(roleAdmin);
        admin1.setUsername("admin_name3");
        admin1.setPassword("admin_password");
        adminRepository.save(admin1);

        roleAccountHolder=new Role("ACCOUNT_HOLDER");
        accountHolder1=new AccountHolder();
        accountHolder1.setRole(roleAccountHolder);
        accountHolder1.setUsername("accountHolder3_name");
        accountHolder1.setPassword("accountHolder_password");
        accountHolderRepository.save(accountHolder1);

        roleThirdParty=new Role("THIRD_PARTY");
        thirdParty3=new ThirdParty();
        thirdParty3.setRole(roleThirdParty);
        thirdParty3.setUsername("thirdParty3_name");
        thirdParty3.setPassword("thirdParty_password");
        thirdPartyRepository.save(thirdParty3);
    }

    @AfterEach
    void tearDown() {
        adminRepository.deleteAll();
        userRepository.deleteAll();
        thirdPartyRepository.deleteAll();
        accountHolderRepository.deleteAll();
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        accountRepository.deleteAll();


    }

    @Test
    void getAllUsers() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/users")).andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("admin_name3"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("accountHolder3_name"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("thirdParty3_name"));
    }

    @Test
    void getAdmins() throws Exception{
        MvcResult mvcResult = mockMvc.perform(get("/users/admins")).andExpect(status().isOk()).andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("admin_name3"));
        assertFalse(mvcResult.getResponse().getContentAsString().contains("accountHolder3_name"));
        assertFalse(mvcResult.getResponse().getContentAsString().contains("thirdParty3_name"));
    }

    @Test
    void getThirdParty() throws Exception{
        MvcResult mvcResult = mockMvc.perform(get("/users/third_party")).andExpect(status().isOk()).andReturn();
        assertFalse(mvcResult.getResponse().getContentAsString().contains("admin_name3"));
        assertFalse(mvcResult.getResponse().getContentAsString().contains("accountHolder3_name"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("thirdParty3_name"));
    }

    @Test
    void getAccountHolders() throws Exception{
        MvcResult mvcResult = mockMvc.perform(get("/users/account_holders")).andExpect(status().isOk()).andReturn();
        assertFalse(mvcResult.getResponse().getContentAsString().contains("admin_name3"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("accountHolder3_name"));
        assertFalse(mvcResult.getResponse().getContentAsString().contains("thirdParty3_name"));
    }

    @Test
    void storeNewAdmin() throws Exception{
        CreateUserDTO admin2=new CreateUserDTO();
            admin2.setRoleType(roleAdmin.getName());
            admin2.setUsername("admin3_name");
            admin2.setPassword("admin_password");
        String body=objectMapper.writeValueAsString(admin2);
        System.out.println(body);
        MvcResult mvcResult=mockMvc.perform(post("/users")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("New ADMIN created"));
        assertTrue(userRepository.findByUsername("admin3_name").isPresent());
        assertEquals(RoleType.ADMIN.toString(), userRepository.findByUsername("admin3_name").get().getRole().getName());
    }

    @Test
    void storeNewAccountHolder() throws Exception{
        CreateUserDTO accountHolder2 =new CreateUserDTO();
        accountHolder2.setRoleType(roleAccountHolder.getName());
        accountHolder2.setUsername("accountHolder2_name");
        accountHolder2.setPassword("accountHolder_password");
        accountHolder2.setDateOfBirth("1980-12-31");
        Address address=new Address();
        address.setCountry("Poland");
        address.setStreet("street_name");
        address.setHomeNumber("100");
        address.setTown("Warsaw");
        address.setZipCode("10-100");
        accountHolder2.setPrimaryAddress(address);

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
        assertEquals("Poland", accountHolderRepository.findByUsername("accountHolder2_name").get().getPrimaryAddress().getCountry());

    }

    @Test
    void storeNewThirdParty() throws Exception{
        CreateUserDTO thirdParty4 =new CreateUserDTO();
        thirdParty4.setRoleType("THIRD_PARTY");
        thirdParty4.setUsername("thirdParty4_name");
        thirdParty4.setPassword("thirdParty_password");

        String body=objectMapper.writeValueAsString(thirdParty4);
        System.out.println(body);
        MvcResult mvcResult=mockMvc.perform(post("/users")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
        assertTrue(mvcResult.getResponse().getContentAsString().contains("New THIRD_PARTY created"));
        assertTrue(userRepository.findByUsername("thirdParty4_name").isPresent());
        assertEquals(RoleType.THIRD_PARTY.toString(), userRepository.findByUsername("thirdParty4_name").get().getRole().getName());
    }
}
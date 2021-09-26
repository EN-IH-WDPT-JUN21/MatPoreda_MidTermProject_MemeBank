package com.ironhack.MemeBank.dto;

import com.ironhack.MemeBank.dao.Address;
import com.ironhack.MemeBank.dao.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateUserDTO {
    private String roleType;
    private Role role;
    private String username;
    private String password;
    private String name;
    private String dateOfBirth;
//    private LocalDate localDateOfBirth;
    private Address primaryAddress;
    private Address mailingAddress;
    private String hashKey;

}

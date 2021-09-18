package com.ironhack.MemeBank.dto;

import com.ironhack.MemeBank.dao.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateUserDTO {
    private String roleType;
    private String username;
    private String password;
    private String name;
    private String dateOfBirth;
    private Address primaryAddress;
    private Address mailingAddress;
    private String hashKey;

}

package com.ironhack.MemeBank.dao.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name="third_party")
public class ThirdParty extends User{
    private String hashKey;
    private String salt;

    public ThirdParty() {
        
    }
}

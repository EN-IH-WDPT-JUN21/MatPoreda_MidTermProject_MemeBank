package com.ironhack.MemeBank.dao.users;

import com.ironhack.MemeBank.dao.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name="account_holder")
public class AccountHolder extends User{
//    @Column(unique=true)
//    private String name;
    private LocalDate dateOfBirth;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn
    private Address primaryAddress;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn
    private Address mailingAddress;

    public AccountHolder() {
    }
}

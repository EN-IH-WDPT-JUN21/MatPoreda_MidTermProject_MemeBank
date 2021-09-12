package com.ironhack.MemeBank.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name= "account_holder")
public class AccountHolder {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long account_holder_id;
    private String name;
    private String dateOfBirth;
    private Address primaryAddress;
    private String mailingAddress;
}

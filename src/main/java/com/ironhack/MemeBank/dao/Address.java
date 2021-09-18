package com.ironhack.MemeBank.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.MemeBank.dao.users.AccountHolder;
import com.ironhack.MemeBank.security.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//@Embeddable
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="address_id")
    private Long id;
    private String country;
    private String town;
    private String street;
    private String homeNumber;
    private String zipCode;



    @OneToMany(mappedBy = "primaryAddress", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<AccountHolder> primaryAddress;

    @OneToMany(mappedBy = "mailingAddress", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<AccountHolder> mailingAddress;

//    @OneToMany(mappedBy = "mailing_address", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<AccountHolder> secondaryHolders = new HashSet<>();



}

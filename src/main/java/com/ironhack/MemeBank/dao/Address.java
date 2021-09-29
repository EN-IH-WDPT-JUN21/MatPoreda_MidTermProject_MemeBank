package com.ironhack.MemeBank.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.MemeBank.dao.users.AccountHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.List;


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


}

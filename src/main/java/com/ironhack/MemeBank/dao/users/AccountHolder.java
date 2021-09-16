package com.ironhack.MemeBank.dao.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.MemeBank.dao.Address;
import com.ironhack.MemeBank.dao.accounts.Account;
import com.ironhack.MemeBank.security.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name="account_holder")
public class AccountHolder extends User{
    private String name;
    private String dateOfBirth;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "country", column = @Column(name = "primary_country")),
            @AttributeOverride( name = "town", column = @Column(name = "primary_town")),
            @AttributeOverride( name = "street", column = @Column(name = "primary_street")),
            @AttributeOverride( name = "homeNumber", column = @Column(name = "primary_homeNumber")),
            @AttributeOverride( name = "zipCode", column = @Column(name = "primary_zipCode")),
    })
    private Address primaryAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "country", column = @Column(name = "mailing_country")),
            @AttributeOverride( name = "town", column = @Column(name = "mailing_town")),
            @AttributeOverride( name = "street", column = @Column(name = "mailing_street")),
            @AttributeOverride( name = "homeNumber", column = @Column(name = "mailing_homeNumber")),
            @AttributeOverride( name = "zipCode", column = @Column(name = "mailing_zipCode")),
    })
    private Address mailingAddress;


//    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JsonIgnore
//    private Set<Account> primaryAccounts = new HashSet<>();

//    @OneToMany(mappedBy = "secondary_owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Set<Account> secondaryAccounts = new HashSet<>();

    public AccountHolder() {
    }

    public Address getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(Address primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public Address getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(Address mailingAddress) {
        this.mailingAddress = mailingAddress;
    }
}

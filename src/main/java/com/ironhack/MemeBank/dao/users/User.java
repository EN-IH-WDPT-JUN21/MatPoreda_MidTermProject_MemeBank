package com.ironhack.MemeBank.dao.users;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.MemeBank.dao.Role;
import com.ironhack.MemeBank.dao.accounts.Account;
import com.ironhack.MemeBank.repository.RoleRepository;
import com.ironhack.MemeBank.security.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Column(name="user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
    private String username;
    private String password;

//    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade=CascadeType.ALL)
//    @JsonIgnore
//    private Set<Role> roles;
//
//    public User(String username, String password, Set<Role> roles) {
//        this.username = username;
//        this.password = password;
//        this.roles = roles;
//    }

    @JsonBackReference
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="role_id")
    private Role role;

    @OneToMany(mappedBy = "primaryOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Account> primaryOwnedAccounts = new HashSet<>();

    @OneToMany(mappedBy = "secondaryOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Account> secondaryOwnedAccounts = new HashSet<>();

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JsonIgnore
//    private Set<UserRoles> userRoles = new HashSet<>();

//    public User(String username, String password, Role role) {
//        this.username = username;
//        this.password = password;
//        this.role = role;
//    }
}
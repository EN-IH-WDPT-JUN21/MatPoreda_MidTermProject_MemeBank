package com.ironhack.MemeBank.dao.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.MemeBank.dao.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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


    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
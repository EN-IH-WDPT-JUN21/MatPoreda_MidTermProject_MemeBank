package com.ironhack.MemeBank.dao;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ironhack.MemeBank.dao.users.User;
import com.ironhack.MemeBank.security.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="role_id")
    private Long id;

//    @Column(unique=true)
    private String name;

//    @ManyToOne
//    @JoinColumn(name="user_id")
//    private User user;
//    @JsonManagedReference
//    @OneToMany(mappedBy = "role", targetEntity = User.class, cascade=CascadeType.ALL, fetch = FetchType.EAGER)
//    private Set<User> users= new HashSet<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserRoles> userRoles = new HashSet<>();

    public Role(String name) {
        this.name = name;
    }
}


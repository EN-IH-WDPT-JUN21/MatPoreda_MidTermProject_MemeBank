package com.ironhack.MemeBank.dao;

import com.ironhack.MemeBank.dao.users.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long roleId;
    private String name;

//    @ManyToOne
//    @JoinColumn(name="user_id")
//    private User user;

    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    private Set<User> users;
}


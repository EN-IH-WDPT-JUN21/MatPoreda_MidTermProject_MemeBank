package com.ironhack.MemeBank.security;

import javax.persistence.*;
import com.ironhack.MemeBank.dao.Role;
import com.ironhack.MemeBank.dao.users.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="user_roles")
public class UserRoles {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userRoleId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    public UserRoles(User user, Role role) {
        this.user = user;
        this.role = role;
    }

}

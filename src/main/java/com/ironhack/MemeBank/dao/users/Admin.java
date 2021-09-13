package com.ironhack.MemeBank.dao.users;

import com.ironhack.MemeBank.dao.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name= "admin")
@AllArgsConstructor
public class Admin extends User {

}

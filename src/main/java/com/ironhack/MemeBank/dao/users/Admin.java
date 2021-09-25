package com.ironhack.MemeBank.dao.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name= "admin")
@AllArgsConstructor
public class Admin extends User {


}

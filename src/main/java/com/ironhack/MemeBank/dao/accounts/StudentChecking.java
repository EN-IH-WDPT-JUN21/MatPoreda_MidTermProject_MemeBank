package com.ironhack.MemeBank.dao.accounts;

import com.ironhack.MemeBank.dao.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@AllArgsConstructor
@Getter
@Setter
@Table(name= "StudentChecking")
public class StudentChecking extends Account{
    @Override
    public Money getMonthlyMaintenanceFee() {
        return null;
    }
}

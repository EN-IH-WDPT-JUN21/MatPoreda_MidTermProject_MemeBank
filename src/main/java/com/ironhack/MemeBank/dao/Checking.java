package com.ironhack.MemeBank.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@Getter
@Setter
@Table(name= "Checking")
public class Checking extends Account {
    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "amount", column = @Column(name = "minimum_balance_amount")),
            @AttributeOverride( name = "currency", column = @Column(name = "minimum_balance_currency")),
    })
    private Money minimumBalance;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "amount", column = @Column(name = "monthly_maintenance_fee_amount")),
            @AttributeOverride( name = "currency", column = @Column(name = "monthly_maintenance_fee_currency")),
    })
    private Money monthlyMaintenanceFee;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "amount", column = @Column(name = "penaltyFee_amount")),
            @AttributeOverride( name = "currency", column = @Column(name = "penaltyFee_currency", insertable=false, updatable=false)),
    })
    @Column(columnDefinition = "numeric default 40")
    private Money penaltyFee;

}

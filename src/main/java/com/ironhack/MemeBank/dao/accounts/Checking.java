package com.ironhack.MemeBank.dao.accounts;

import com.ironhack.MemeBank.dao.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@Getter
@Setter
@Table(name= "Checking")
public class Checking extends Account {
    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "amount", column = @Column(name = "minimum_balance_amount", columnDefinition = "decimal(19,10) default 0.00", precision = 10, scale = 10)),
            @AttributeOverride( name = "currency", column = @Column(name = "minimum_balance_currency")),
    })
    private Money minimumBalance=new Money(new BigDecimal("0"));

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "amount", column = @Column(name = "monthly_maintenance_fee_amount", columnDefinition = "decimal(19,10) default 0.00", precision = 10, scale = 10)),
            @AttributeOverride( name = "currency", column = @Column(name = "monthly_maintenance_fee_currency")),
    })
    private Money monthlyMaintenanceFee=new Money(new BigDecimal("0"));

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "amount", column = @Column(name = "penaltyFee_amount", columnDefinition = "decimal(19,10) default 0.00", precision = 10, scale = 10)),
            @AttributeOverride( name = "currency", column = @Column(name = "penaltyFee_currency", insertable=false, updatable=false)),
    })
    @Column(columnDefinition = "numeric default 40")
    private Money penaltyFee=new Money(new BigDecimal("40"));

    public Checking() {
    }
}

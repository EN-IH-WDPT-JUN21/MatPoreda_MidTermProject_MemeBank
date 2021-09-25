package com.ironhack.MemeBank.dao.accounts;

import com.ironhack.MemeBank.dao.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@Getter
@Setter
@Table(name= "credit_card")
public class CreditCard extends Account{

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "amount", column = @Column(name = "creditLimit_amount", columnDefinition = "decimal(19,10) default 0.00", precision = 10, scale = 10)),
            @AttributeOverride( name = "currency", column = @Column(name = "creditLimit_currency")),
    })
    @Column(columnDefinition = "numeric default 100")
    private Money creditLimit;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "amount", column = @Column(name = "penaltyFee_amount", columnDefinition = "decimal(19,10) default 0.00", precision = 10, scale = 10)),
            @AttributeOverride( name = "currency", column = @Column(name = "penaltyFee_currency", insertable=false, updatable=false)),
    })
    @Column(columnDefinition = "numeric default 40")
    private Money penaltyFee;

    @DecimalMin(value="0.1", message="Interest rate for credit card cannot be lower than 0.1")
    @Column(columnDefinition = "decimal(19,10) default 0.2")
    private BigDecimal interestRate;
    public CreditCard() {
    }

    @Override
    public Money getMonthlyMaintenanceFee() {
        return //this.monthlyMaintenanceFee;
        null;
    }
}

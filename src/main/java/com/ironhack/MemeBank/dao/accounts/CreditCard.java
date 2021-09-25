package com.ironhack.MemeBank.dao.accounts;

import com.ironhack.MemeBank.dao.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@Getter
@Setter
@Table(name= "credit_card")
public class CreditCard extends Account{

//    @Id
//    @GeneratedValue(strategy= GenerationType.IDENTITY)
//    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "amount", column = @Column(name = "creditLimit_amount")),
            @AttributeOverride( name = "currency", column = @Column(name = "creditLimit_currency")),
    })
    @Column(columnDefinition = "numeric default 100")
    private Money creditLimit;

//    @Embedded
//    @AttributeOverrides({
//            @AttributeOverride( name = "amount", column = @Column(name = "monthly_maintenance_fee_amount")),
//            @AttributeOverride( name = "currency", column = @Column(name = "monthly_maintenance_fee_currency")),
//    })
//    @Column(columnDefinition = "numeric default 5")
//    private Money monthlyMaintenanceFee;


    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "amount", column = @Column(name = "penaltyFee_amount")),
            @AttributeOverride( name = "currency", column = @Column(name = "penaltyFee_currency", insertable=false, updatable=false)),
    })
    @Column(columnDefinition = "numeric default 40")
    private Money penaltyFee;

    @DecimalMin(value="0.1", inclusive=true, message="Interest rate for credit card cannot be lower than 0.1")
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

package com.ironhack.MemeBank.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@Getter
@Setter
@Table(name= "Savings")
public class Savings extends Account {
    @DecimalMin(value="0.0000", inclusive=true, message="Interest rate cannot be negative")
    @DecimalMax(value="0.05", inclusive=true, message="Interest rate cannot be higher than 0.05")
    @Column(columnDefinition = "decimal default 0.0025")
    private BigDecimal interestRate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "amount", column = @Column(name = "minimum_balance_amount")),
            @AttributeOverride( name = "currency", column = @Column(name = "minimum_balance_currency")),
    })
    @Column(columnDefinition = "numeric default 1000")
    @Min(value=100, message = "Minimum balance for savings account is 100")
    private Money minimumBalance;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "amount", column = @Column(name = "penaltyFee_amount")),
            @AttributeOverride( name = "currency", column = @Column(name = "penaltyFee_currency", insertable=false, updatable=false)),
    })
    @Column(columnDefinition = "numeric default 40")
    private Money penaltyFee;

}

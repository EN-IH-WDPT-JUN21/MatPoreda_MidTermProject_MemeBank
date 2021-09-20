package com.ironhack.MemeBank.dao.accounts;

import com.ironhack.MemeBank.dao.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

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
    @DecimalMin(value="0.0025", inclusive=true, message="Interest rate cannot be negative, or lower than 0.0025")
    @DecimalMax(value="0.5", inclusive=true, message="Interest rate cannot be higher than 0.5")
    @Column(columnDefinition = "decimal(19,8) default 0.0025", precision = 10, scale = 5)
    @Type(type = "big_decimal")
    private BigDecimal interestRate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "amount", column = @Column(name = "minimum_balance_amount",columnDefinition = "decimal(19,8) default 1000.00", precision = 10, scale = 5)),
            @AttributeOverride( name = "currency", column = @Column(name = "minimum_balance_currency")),
    })
    private Money minimumBalance;

//    @Embedded
//    @AttributeOverrides({
//            @AttributeOverride( name = "amount", column = @Column(name = "penaltyFee_amount")),
//            @AttributeOverride( name = "currency", column = @Column(name = "penaltyFee_currency", insertable=false, updatable=false)),
//    })
//    @Column(columnDefinition = "numeric default 40")
//    private Money penaltyFee;

    public Savings() {
    }
}

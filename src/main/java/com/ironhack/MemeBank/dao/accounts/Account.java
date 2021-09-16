package com.ironhack.MemeBank.dao.accounts;

import com.ironhack.MemeBank.dao.Money;
import com.ironhack.MemeBank.dao.Transaction;
import com.ironhack.MemeBank.dao.users.AccountHolder;
import com.ironhack.MemeBank.enums.Status;
import com.ironhack.MemeBank.security.SecretKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class Account {

    @Id
    @Column(name="account_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "amount", column = @Column(name = "balance_amount")),
            @AttributeOverride( name = "currency", column = @Column(name = "balance_currency")),
    })
    private Money balance;

    @Embedded
    private SecretKey secretKey;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "amount", column = @Column(name = "penalty_fee_amount")),
            @AttributeOverride( name = "currency", column = @Column(name = "penalty_fee_currency")),
    })
    private Money penaltyFee;

    private LocalDate creationDate;

    private Status status;

//    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Transaction> transactionList;
}

package com.ironhack.MemeBank.dao;

import com.ironhack.MemeBank.enums.Status;
import com.ironhack.MemeBank.security.SecretKey;
import lombok.Getter;

import javax.persistence.*;
import java.util.Optional;

@MappedSuperclass
public abstract class Account {

    @Id
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "primary_account_holder_id")
    private AccountHolder primaryOwner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "secondary_account_holder_id")
    private AccountHolder secondaryOwner;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "amount", column = @Column(name = "penalty_fee_amount")),
            @AttributeOverride( name = "currency", column = @Column(name = "penalty_fee_currency")),
    })
    private Money penaltyFee;

    private String creationDate;

    private Status status;
}

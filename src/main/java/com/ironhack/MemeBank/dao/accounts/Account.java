package com.ironhack.MemeBank.dao.accounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.MemeBank.dao.Money;
import com.ironhack.MemeBank.dao.Transaction;
import com.ironhack.MemeBank.dao.users.User;
import com.ironhack.MemeBank.enums.AccountType;
import com.ironhack.MemeBank.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
            @AttributeOverride( name = "amount", column = @Column(name = "balance_amount",columnDefinition = "decimal(19,10) default 0.00", precision = 10, scale = 10)),
            @AttributeOverride( name = "currency", column = @Column(name = "balance_currency")),
    })
    private Money balance;
    private String secretKey;
    private byte[] salt;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "amount", column = @Column(name = "penalty_fee_amount",columnDefinition = "decimal(19,10) default 40.00", precision = 10, scale = 10)),
            @AttributeOverride( name = "currency", column = @Column(name = "penalty_fee_currency")),
    })
    private Money penaltyFee;
    private LocalDate creationDate;
    private Status status;

    @Enumerated
    private AccountType accountType;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(nullable=false)
    private User primaryOwner;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn
    private User secondaryOwner;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Transaction> transactionList= new HashSet<>();
    public abstract Money getMonthlyMaintenanceFee();

}

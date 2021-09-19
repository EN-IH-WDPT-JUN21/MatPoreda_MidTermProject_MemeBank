package com.ironhack.MemeBank.dao.accounts;

import com.ironhack.MemeBank.dao.Money;
import com.ironhack.MemeBank.dao.Transaction;
import com.ironhack.MemeBank.dao.users.AccountHolder;
import com.ironhack.MemeBank.dao.users.User;
import com.ironhack.MemeBank.enums.Status;
import com.ironhack.MemeBank.security.SecretKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
            @AttributeOverride( name = "amount", column = @Column(name = "balance_amount")),
            @AttributeOverride( name = "currency", column = @Column(name = "balance_currency")),
    })
    private Money balance;

//    @Embedded
//    private byte[] secretKey;
//    private byte[] salt;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "amount", column = @Column(name = "penalty_fee_amount")),
            @AttributeOverride( name = "currency", column = @Column(name = "penalty_fee_currency")),
    })
    @Column(columnDefinition = "numeric default 40")
    private Money penaltyFee;

    private Date creationDate;

    private Status status;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(nullable=false)
    private User primaryOwner;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(nullable=true)
    private User secondaryOwner;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Transaction> transactionList= new HashSet<>();

}

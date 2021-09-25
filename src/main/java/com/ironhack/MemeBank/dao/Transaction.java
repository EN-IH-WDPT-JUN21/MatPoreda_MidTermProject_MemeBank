package com.ironhack.MemeBank.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.MemeBank.dao.accounts.Account;
import com.ironhack.MemeBank.dao.users.User;
import com.ironhack.MemeBank.enums.TransactionStatus;
import com.ironhack.MemeBank.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime date;
    private String description;
    private TransactionType type;
    private TransactionStatus status;
    private Money amount;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(nullable=true)
    @JsonIgnore
    private Account account;
    private String responseStatus;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(nullable=true)
    @JsonIgnore
    private Account transactionInitiatorAccount;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(nullable=true)
    @JsonIgnore
    private User transactionInitiator;


}

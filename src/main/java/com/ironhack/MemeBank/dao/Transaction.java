package com.ironhack.MemeBank.dao;

import com.ironhack.MemeBank.dao.accounts.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private String description;
    private String type;
    private String status;
    private Money amount;
    private BigDecimal availableBalance;
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(nullable=true)
    private Account account;

}

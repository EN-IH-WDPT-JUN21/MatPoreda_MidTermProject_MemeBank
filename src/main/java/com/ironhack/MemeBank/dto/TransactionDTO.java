package com.ironhack.MemeBank.dto;

import com.ironhack.MemeBank.dao.Money;
import com.ironhack.MemeBank.dao.accounts.Account;
import com.ironhack.MemeBank.enums.TransactionStatus;
import com.ironhack.MemeBank.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionDTO {
    private LocalDateTime date;
    private String description;
    private String type;
    private String status;
    private String amount;
    private String availableBalance;
    private String accountId;
    private String secretKey;
    private String transactionInitiatorAccountId;
    private String transactionInitiatorUserId;
    private String hashKey;
    private String ownerName;

}

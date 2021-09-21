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
    private Optional<String> date;
    private Optional<String> description;
    private Optional<String> type;
    private Optional<String> status;
    private Optional<String> amount;
    private Optional<String> availableBalance;
    private Optional<String> accountId;
    private Optional<String> secretKey;
}

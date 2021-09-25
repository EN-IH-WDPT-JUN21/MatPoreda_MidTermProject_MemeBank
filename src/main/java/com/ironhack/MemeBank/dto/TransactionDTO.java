package com.ironhack.MemeBank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

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
    private String balance;

}

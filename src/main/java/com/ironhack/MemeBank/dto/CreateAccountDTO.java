package com.ironhack.MemeBank.dto;

import com.ironhack.MemeBank.dao.Money;
import com.ironhack.MemeBank.dao.users.User;
import com.ironhack.MemeBank.enums.Status;
import com.ironhack.MemeBank.security.SecretKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateAccountDTO {
    @NotBlank(message="Account type cannot be empty")
    private String accountType;
    private String balance;
    private String penaltyFee;
    private String creationDate;
//    private Status status;
    private String minimumBalance;
    private String monthlyMaintenanceFee;
    private String interestRate;
    private String creditLimit;
    @NotBlank
    private String primaryOwnerName;
    private String secondaryOwnerName;

}

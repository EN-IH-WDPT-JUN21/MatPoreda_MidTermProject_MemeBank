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
    private Optional<String> balance;
    private Optional<String> penaltyFee;
    private Optional<String> creationDate;
//    private Status status;
    private Optional<String> minimumBalance;
    private Optional<String> monthlyMaintenanceFee;
    private Optional<String> interestRate;
    private Optional<String> creditLimit;
    @NotBlank
    private String primaryOwnerName;
    private Optional<String> secondaryOwnerName;

}

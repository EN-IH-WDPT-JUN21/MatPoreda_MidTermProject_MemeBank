package com.ironhack.MemeBank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotBlank;

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
    private String minimumBalance;
    private String monthlyMaintenanceFee;
    private String interestRate;
    private String creditLimit;
    @NotBlank
    private String primaryOwnerName;
    private String secondaryOwnerName;

}

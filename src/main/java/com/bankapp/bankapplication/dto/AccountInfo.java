package com.bankapp.bankapplication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfo {
    @Schema(
            name = "User Account Name"
    )
    private String accountName;

    @Schema(
            name = "User Account Balance"
    )
    private BigDecimal accountBalance;

    @Schema(
            name = "User Account Number"
    )
    private String accountNumber;

    @Schema(
            name = "Transaction History",
            description = "Details of the recent transaction"
    )
    private String transactionHistory; // Added this field to hold the transaction history
}

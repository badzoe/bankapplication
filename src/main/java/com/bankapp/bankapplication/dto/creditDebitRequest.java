package com.bankapp.bankapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class creditDebitRequest {

    private String accountNumber;
    private BigDecimal amount;
}

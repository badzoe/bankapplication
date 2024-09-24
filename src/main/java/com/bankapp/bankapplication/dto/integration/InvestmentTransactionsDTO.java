package com.bankapp.bankapplication.dto.integration;

import java.math.BigDecimal;

public record InvestmentTransactionsDTO(char transactionType,Long productID, BigDecimal balance) {
}

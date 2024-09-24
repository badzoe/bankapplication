package com.bankapp.bankapplication.dto.integration;

import java.math.BigDecimal;

public record InvestProductRequest(Long investorID, Long productID, BigDecimal balance) {
}

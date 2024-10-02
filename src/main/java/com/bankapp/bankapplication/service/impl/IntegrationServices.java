package com.bankapp.bankapplication.service.impl;

import com.bankapp.bankapplication.dto.integration.InvestmentTransactionsDTO;
import com.bankapp.bankapplication.dto.integration.InvestorCreation;
import org.springframework.http.ResponseEntity;

public interface IntegrationServices {

    String loginToInvestmentService(String username, String password);
    ResponseEntity<String> transactOnInvestments(InvestmentTransactionsDTO investmentTransactionsDTO, String token);
    ResponseEntity<String> registerForInvestments(InvestorCreation investorCreation);
}
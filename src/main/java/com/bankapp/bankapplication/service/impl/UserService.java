package com.bankapp.bankapplication.service.impl;

import com.bankapp.bankapplication.dto.*;
import com.bankapp.bankapplication.dto.integration.InvestmentTransactionsDTO;

public interface UserService {

    BankResponce createAccount(UserRequest userRequest);
    BankResponce balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
    BankResponce creditAccount(creditDebitRequest request);
    BankResponce debitAccount(creditDebitRequest request);
    BankResponce transfer(TransferRequest request);
    BankResponce login(LoginDto loginDto);
    BankResponce processInvestmentTransaction(String token, InvestmentTransactionsDTO investmentTransactionsDTO);

}

package com.bankapp.bankapplication.service.impl;

import com.bankapp.bankapplication.dto.TransactionDto;
import com.bankapp.bankapplication.entity.Transaction;

public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}

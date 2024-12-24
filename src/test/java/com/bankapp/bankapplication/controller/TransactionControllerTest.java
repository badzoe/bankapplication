package com.bankapp.bankapplication.controller;

import com.bankapp.bankapplication.entity.Transaction;
import com.bankapp.bankapplication.service.impl.BankStatement;
import com.itextpdf.text.DocumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TransactionControllerTest {

    @Mock
    private BankStatement bankStatement;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateBankStatement_Success() throws FileNotFoundException, DocumentException {
        // Arrange
        String accountNumber = "2024123456";
        String startDate = "2023-01-01";
        String endDate = "2023-01-31";
        List<Transaction> mockTransactions = Arrays.asList(
                new Transaction(/*...initialize transaction details here...*/),
                new Transaction(/*...initialize another transaction...*/)
        );
        when(bankStatement.generateStatement(accountNumber, startDate, endDate)).thenReturn(mockTransactions);

        // Act
        List<Transaction> result = transactionController.generateBankStatement(accountNumber, startDate, endDate);

        // Assert
        assertEquals(mockTransactions.size(), result.size());
    }

    @Test
    void testGenerateBankStatement_FileNotFoundException() throws FileNotFoundException, DocumentException {
        // Arrange
        String accountNumber = "2024123456";
        String startDate = "2023-01-01";
        String endDate = "2023-01-31";
        when(bankStatement.generateStatement(accountNumber, startDate, endDate))
                .thenThrow(new FileNotFoundException("File not found"));

        // Act
        ResponseEntity<Map<String, String>> response = transactionController.handleFileNotFoundException(new FileNotFoundException("File not found"));

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("File not found", response.getBody().get("message"));
    }

    @Test
    void testGenerateBankStatement_DocumentException() throws FileNotFoundException, DocumentException {
        // Arrange
        String accountNumber = "2024123456";
        String startDate = "2023-01-01";
        String endDate = "2023-01-31";
        when(bankStatement.generateStatement(accountNumber, startDate, endDate))
                .thenThrow(new DocumentException("Error generating document"));

        // Act
        ResponseEntity<Map<String, String>> response = transactionController.handleDocumentException(new DocumentException("Error generating document"));

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error generating document", response.getBody().get("message"));
    }
}

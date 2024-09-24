package com.bankapp.bankapplication.controller;

import com.bankapp.bankapplication.entity.Transaction;
import com.bankapp.bankapplication.service.impl.BankStatement;
import com.itextpdf.text.DocumentException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bankStatement")
@AllArgsConstructor
public class TransactionController {

    private BankStatement bankStatement;

    @GetMapping
    public List<Transaction> generateBankStatement(@RequestParam String accountNumber,
                                                                 @RequestParam String startDate,
                                                                 @RequestParam String endDate)
            throws DocumentException, FileNotFoundException {
        return bankStatement.generateStatement(accountNumber, startDate, endDate);
    }

    // Global exception handling for FileNotFoundException
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleFileNotFoundException(FileNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // Exception handling for DocumentException
    @ExceptionHandler(DocumentException.class)
    public ResponseEntity<Map<String, String>> handleDocumentException(DocumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

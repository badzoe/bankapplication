package com.bankapp.bankapplication.controller;

import com.bankapp.bankapplication.dto.*;
import com.bankapp.bankapplication.dto.integration.InvestmentTransactionsDTO;
import com.bankapp.bankapplication.service.impl.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAccount() {
        // Arrange
        UserRequest userRequest = new UserRequest();
        BankResponce mockResponse = BankResponce.builder()
                .responceCode("201")
                .responceMessage("Account Created")
                .build();
        when(userService.createAccount(any(UserRequest.class))).thenReturn(mockResponse);

        // Act
        BankResponce response = userController.createAccount(userRequest);

        // Assert
        assertEquals("201", response.getResponceCode());
        assertEquals("Account Created", response.getResponceMessage());
    }

    @Test
    void testLogin() {
        // Arrange
        LoginDto loginDto = new LoginDto();
        BankResponce mockResponse = BankResponce.builder()
                .responceCode("200")
                .responceMessage("Login Successful")
                .build();
        when(userService.login(any(LoginDto.class))).thenReturn(mockResponse);

        // Act
        BankResponce response = userController.login(loginDto);

        // Assert
        assertEquals("200", response.getResponceCode());
        assertEquals("Login Successful", response.getResponceMessage());
    }

    @Test
    void testBalanceEnquiry() {
        // Arrange
        EnquiryRequest enquiryRequest = new EnquiryRequest();
        BankResponce mockResponse = BankResponce.builder()
                .responceCode("200")
                .responceMessage("Balance Retrieved")
                .build();
        when(userService.balanceEnquiry(any(EnquiryRequest.class))).thenReturn(mockResponse);

        // Act
        BankResponce response = userController.balanceEnquiry(enquiryRequest);

        // Assert
        assertEquals("200", response.getResponceCode());
        assertEquals("Balance Retrieved", response.getResponceMessage());
    }

    @Test
    void testNameEnquiry() {
        // Arrange
        EnquiryRequest enquiryRequest = new EnquiryRequest();
        when(userService.nameEnquiry(any(EnquiryRequest.class))).thenReturn("John Doe");

        // Act
        String response = userController.nameEnquiry(enquiryRequest);

        // Assert
        assertEquals("John Doe", response);
    }

    @Test
    void testCreditAccount() {
        // Arrange
        creditDebitRequest request = new creditDebitRequest();
        BankResponce mockResponse = BankResponce.builder()
                .responceCode("200")
                .responceMessage("Account Credited")
                .build();
        when(userService.creditAccount(any(creditDebitRequest.class))).thenReturn(mockResponse);

        // Act
        BankResponce response = userController.creditAccount(request);

        // Assert
        assertEquals("200", response.getResponceCode());
        assertEquals("Account Credited", response.getResponceMessage());
    }

    @Test
    void testDebitAccount() {
        // Arrange
        creditDebitRequest request = new creditDebitRequest();
        BankResponce mockResponse = BankResponce.builder()
                .responceCode("200")
                .responceMessage("Account Debited")
                .build();
        when(userService.debitAccount(any(creditDebitRequest.class))).thenReturn(mockResponse);

        // Act
        BankResponce response = userController.debitAccount(request);

        // Assert
        assertEquals("200", response.getResponceCode());
        assertEquals("Account Debited", response.getResponceMessage());
    }

    @Test
    void testTransfer() {
        // Arrange
        TransferRequest transferRequest = new TransferRequest();
        BankResponce mockResponse = BankResponce.builder()
                .responceCode("200")
                .responceMessage("Transfer Successful")
                .build();
        when(userService.transfer(any(TransferRequest.class))).thenReturn(mockResponse);

        // Act
        BankResponce response = userController.transfer(transferRequest);

        // Assert
        assertEquals("200", response.getResponceCode());
        assertEquals("Transfer Successful", response.getResponceMessage());
    }

    @Test
    void testInvestments() {
        // Arrange
        InvestmentTransactionsDTO investmentTransactionsDTO = new InvestmentTransactionsDTO();
        BankResponce mockResponse = BankResponce.builder()
                .responceCode("200")
                .responceMessage("Investment Processed")
                .build();
        when(userService.processInvestmentTransaction(any(String.class), any(InvestmentTransactionsDTO.class))).thenReturn(mockResponse);

        // Act
        BankResponce response = userController.investments(investmentTransactionsDTO, "Bearer token");

        // Assert
        assertEquals("200", response.getResponceCode());
        assertEquals("Investment Processed", response.getResponceMessage());
    }
}

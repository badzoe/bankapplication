package com.bankapp.bankapplication.controller;

import com.bankapp.bankapplication.dto.*;
import com.bankapp.bankapplication.dto.integration.InvestmentTransactionsDTO;
import com.bankapp.bankapplication.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( "/api/user")
@Tag(name = "User Account Management APIs")
public class UserController {

    @Autowired
    UserService userService;

    @Operation(
            summary = "Create New User Account",
            description = "Creating a new user and assigning an account ID"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 CREATED"
    )
    @PostMapping
    public BankResponce createAccount(@RequestBody UserRequest userRequest){
        return userService.createAccount(userRequest);
    }

    @PostMapping("/login")
    public BankResponce login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }

    @Operation(
            summary = "Balance Enquiry",
            description = "Given an account number, check how much the user has"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 201 SUCCESS"
    )
    @GetMapping("/balanceEnquiry")
    public BankResponce balanceEnquiry (@RequestBody EnquiryRequest request){
        return userService.balanceEnquiry(request);
    }

    @GetMapping("/nameEnquiry")
    public String nameEnquiry (@RequestBody EnquiryRequest request){
        return userService.nameEnquiry(request);
    }

    @PostMapping("credit")
    public BankResponce creditAccount(@RequestBody creditDebitRequest request){
        return userService.creditAccount(request);
    }

    @PostMapping("debit")
    public BankResponce debitAccount(@RequestBody creditDebitRequest request){
        return userService.debitAccount(request);
    }

    @PostMapping("transfer")
    public BankResponce transfer(@RequestBody TransferRequest request){
        return userService.transfer(request);
    }

    @PostMapping("/investments")
    public BankResponce investments(@RequestBody InvestmentTransactionsDTO request, @RequestHeader("Authorization") String authorization){

        return userService.processInvestmentTransaction(authorization.replace("Bearer ",""),request);
    }
}

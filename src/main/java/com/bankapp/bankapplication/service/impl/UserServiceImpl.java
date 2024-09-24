package com.bankapp.bankapplication.service.impl;

import com.bankapp.bankapplication.config.JwtTokenProvider;
import com.bankapp.bankapplication.dto.*;
import com.bankapp.bankapplication.dto.integration.InvestmentTransactionsDTO;
import com.bankapp.bankapplication.entity.Role;
import com.bankapp.bankapplication.entity.User;
import com.bankapp.bankapplication.entity.Tokens;
import com.bankapp.bankapplication.repository.UserRepository;
import com.bankapp.bankapplication.repository.TokensRepository;
import com.bankapp.bankapplication.utils.AccountUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    private final IntegrationServicesImpl integrationServices;
    private final TokensRepository tokensRepository;

    @Override
    public BankResponce createAccount(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponce.builder()
                    .responceCode(AccountUtils.ACCOUNT_EXIST_CODE)
                    .responceMessage(AccountUtils.ACCOUNT_EXISTS_MASSAGE)
                    .accountInfo(null)
                    .build();

        }
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativeNumber(userRequest.getAlternativeNumber())
                .status("ACTIVE")
                .role(Role.valueOf("ACCOUNT_ADMIN"))
                .build();

        User savedUser = userRepository.save(newUser);

        EmailDetails emailDetails = EmailDetails.builder()

                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations! Your Account has been successfully created.\n Your Account Details: \n" + "Account Name:" + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() + "\nAccount Number:" + savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);
        return BankResponce.builder()
                .responceCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responceMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getFirstName() + " " + savedUser.getOtherName())
                        .build())
                .build();

    }

    public BankResponce login(LoginDto loginDto) {
        log.info("test");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );
            if(authentication.isAuthenticated())
            {

            }else {

            }

            String result =integrationServices.loginToInvestmentService(loginDto.getEmail(), loginDto.getPassword());
            log.info(result);

            EmailDetails loginAlert = EmailDetails.builder()
                    .subject("You are logged in!")
                    .recipient(loginDto.getEmail())
                    .messageBody("You logged into your account. If you did not initiate this request, please contact the bank!")
                    .build();
            //emailService.sendEmailAlert(loginAlert);
            return BankResponce.builder()
                    .responceCode("Login Success")
                    .responceMessage(jwtTokenProvider.generateToken(authentication))
                    .build();
        }catch (Exception e){
            log.info(e.toString());
            return null;
        }
    }

    @Override
    public BankResponce processInvestmentTransaction(String token,InvestmentTransactionsDTO transactionsDTO) {

        Optional<Tokens> lastToken = tokensRepository.findFirstByUser_IdOrderByIdDesc(jwtTokenProvider.getUserIdFromToken(token));
        if(lastToken.isPresent()){
            log.info("Found a token in your name but pending validity check");
            if(jwtTokenProvider.isTokenExpired(lastToken.get().getToken())){
                log.info("Token expired");
                return new BankResponce("400","Session expired. Login to perform transaction",null);
            }    else {

                ResponseEntity<String> response =  integrationServices.transactOnInvestments(transactionsDTO,lastToken.get().getToken().toString());
                return new BankResponce(response.getStatusCode().toString(), response.getBody(), null);


            }
        }{
            log.info("Couldn't find the token to Investment service");
            return new BankResponce("400","You don't have  a valid token to perform transaction",null);
        }
    }

    @Override
    public BankResponce balanceEnquiry(EnquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponce.builder()

                    .responceCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responceMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();

        }

        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponce.builder()
                .responceCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responceMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();
    }

    @Override
    public BankResponce creditAccount(creditDebitRequest request) {
        // Checking if the account exists
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponce.builder()
                    .responceCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responceMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());

        // Update the account balance
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        // Save transaction history
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto);

        // Return the response
        return BankResponce.builder()
                .responceCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responceMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponce debitAccount(creditDebitRequest request) {
        // checking if the account exists
        // check if the withdrawal amount is not more than the current balance

        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponce.builder()

                    .responceCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responceMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();

        if (availableBalance.intValue() < debitAmount.intValue()) {
            return BankResponce.builder()
                    .responceCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responceMessage(AccountUtils.INSUFFICIENT_BALANCE_MASSAGE)
                    .accountInfo(null)

                    .build();
        } else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);
            TransactionDto transactionDto = TransactionDto.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("DEBIT")
                    .amount(request.getAmount())
                    .build();
            transactionService.saveTransaction(transactionDto);
            return BankResponce.builder()
                    .responceCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                    .responceMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(request.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();
        }
    }
    @Override
    public BankResponce transfer(TransferRequest request) {
        // Check if the destination account exists
        boolean isDestinationAccountExist = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if (!isDestinationAccountExist) {
            return BankResponce.builder()
                    .responceCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responceMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        // Get the source account
        User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        if (sourceAccountUser == null) {
            return BankResponce.builder()
                    .responceCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responceMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        // Check if the amount to be debited is not more than the current balance
        if (request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0) {
            return BankResponce.builder()
                    .responceCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responceMessage(AccountUtils.INSUFFICIENT_BALANCE_MASSAGE)
                    .accountInfo(null)
                    .build();
        }

        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(sourceAccountUser);

        User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
        userRepository.save(destinationAccountUser);

        // Send debit alert to source account holder
        EmailDetails debitAlert = EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("The amount of " + request.getAmount() + " has been deducted from your account! Your current balance is " + sourceAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(debitAlert);

        // Send credit alert to destination account holder
        EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipient(destinationAccountUser.getEmail())
                .messageBody("The amount of " + request.getAmount() + " has been credited to your account! Your current balance is " + destinationAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(creditAlert);

        TransactionDto debitTransaction = TransactionDto.builder()
                .accountNumber(sourceAccountUser.getAccountNumber())
                .transactionType("TRANSFER")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(debitTransaction);


        String transferHistory = "Transfer from " + sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName() + " to " +
                destinationAccountUser.getFirstName() + " " + destinationAccountUser.getLastName() +
                " of amount " + request.getAmount() + ". New balance: " + sourceAccountUser.getAccountBalance();

        return BankResponce.builder()
                .responceCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responceMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(sourceAccountUser.getAccountNumber())
                        .accountBalance(sourceAccountUser.getAccountBalance())
                        .transactionHistory(transferHistory) // Include the transfer history in the response
                        .build())
                .build();
    }

    // balance enquiry, name enquiry, credit, debit, transfer
}
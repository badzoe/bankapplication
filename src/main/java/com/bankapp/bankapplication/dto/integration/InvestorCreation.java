package com.bankapp.bankapplication.dto.integration;

public record InvestorCreation(
        String name,
        String surname,
        String dateOfBirth,
        String address,
        String phoneNumber,
        String email,
        String password
) {
}

package com.bankapp.bankapplication.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    private String firstName;
    private String lastName;
    private String otherName;
    private String gender;
    private String address;
    private String stateOfOrigin;
    private String email;
    private String password;
    private String phoneNumber;
    private String alternativeNumber;
    private String dateOfBirth;
    private Boolean enableInvestmentsLogin;
}

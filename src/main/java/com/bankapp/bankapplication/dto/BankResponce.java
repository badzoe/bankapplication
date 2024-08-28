package com.bankapp.bankapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor

public class BankResponce {

    private String responceCode;
    private String responceMessage;
    private AccountInfo accountInfo;
}

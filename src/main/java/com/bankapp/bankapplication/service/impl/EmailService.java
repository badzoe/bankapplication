package com.bankapp.bankapplication.service.impl;

import com.bankapp.bankapplication.dto.EmailDetails;

public interface EmailService {

    void sendEmailAlert(EmailDetails emailDetails);
    void sendEmailWithAttachment(EmailDetails emailDetails);
}

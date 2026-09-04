package com.nabgha.book.auth.infrastructure.email;

import com.nabgha.book.auth.domain.repository.EmailSenderPort;
import com.nabgha.book.shared.email.EmailService;
import com.nabgha.book.shared.email.EmailTemplateName;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Component;

@Component
public class EmailSenderAdapter implements EmailSenderPort {

    private final EmailService emailService;

    public EmailSenderAdapter(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void sendActivationEmail(String toEmail, String fullName, String activationCode, String activationUrl) throws MessagingException {
        String fullActivationUrl = activationUrl + "?token=" + activationCode;
        emailService.sendEmail(toEmail, fullName, EmailTemplateName.ACTIVATE_ACCOUNT, fullActivationUrl, activationCode, "Account activation" );
    }
}

package com.nabgha.book.shared.email.listener;

import com.nabgha.book.auth.domain.event.UserRegisteredEvent;
import com.nabgha.book.shared.email.EmailService;
import com.nabgha.book.shared.email.EmailTemplateName;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationEmailListener {

    private final EmailService emailService;

    @ApplicationModuleListener
    public void onUserRegistered(UserRegisteredEvent event) {
        log.info("Received UserRegisteredEvent for email: {}", event.email());
        try {
            String fullActivationUrl = event.activationUrl() + "?token=" + event.activationCode();
            emailService.sendEmail(
                    event.email(),
                    event.fullName(),
                    EmailTemplateName.ACTIVATE_ACCOUNT,
                    fullActivationUrl,
                    event.activationCode(),
                    "Account activation"
            );
            log.info("Activation email successfully sent to {}", event.email());
        } catch (MessagingException e) {
            log.error("Failed to send activation email to {}", event.email(), e);
        }
    }
}

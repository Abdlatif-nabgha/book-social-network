package com.nabgha.book.auth.infrastructure.email;

import com.nabgha.book.auth.domain.event.UserRegisteredEvent;
import com.nabgha.book.auth.domain.repository.EmailSenderPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EmailSenderAdapter implements EmailSenderPort {

    private final ApplicationEventPublisher eventPublisher;

    public EmailSenderAdapter(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void sendActivationEmail(String toEmail, String fullName, String activationCode, String activationUrl) {
        eventPublisher.publishEvent(new UserRegisteredEvent(toEmail, fullName, activationCode, activationUrl));
    }
}

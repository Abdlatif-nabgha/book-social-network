package com.nabgha.book.auth.domain.repository;


import jakarta.mail.MessagingException;

public interface EmailSenderPort {
    void sendActivationEmail(String toEmail, String fullName,String activationCode, String activationUrl) throws MessagingException;
}

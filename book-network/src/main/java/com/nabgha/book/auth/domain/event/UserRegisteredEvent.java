package com.nabgha.book.auth.domain.event;

public record UserRegisteredEvent(
        String email,
        String fullName,
        String activationCode,
        String activationUrl
) {
}

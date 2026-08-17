package com.nabgha.book.auth.infrastructure.security;

import com.nabgha.book.auth.domain.repository.ActivationCodeGeneratorPort;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ActivationCodeGeneratorAdapter implements ActivationCodeGeneratorPort {

    @Override
    public String generate(int length) {
        String characters = "0123456789";
        StringBuilder result = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString();
    }
}

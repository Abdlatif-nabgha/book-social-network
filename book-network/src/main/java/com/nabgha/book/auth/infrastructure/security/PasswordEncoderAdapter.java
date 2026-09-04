package com.nabgha.book.auth.infrastructure.security;


import com.nabgha.book.auth.domain.repository.PasswordEncoderPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderAdapter implements PasswordEncoderPort{

    private final PasswordEncoder passwordEncoder;

    public PasswordEncoderAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}

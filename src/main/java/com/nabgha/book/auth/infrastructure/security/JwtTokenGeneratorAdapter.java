package com.nabgha.book.auth.infrastructure.security;


import com.nabgha.book.auth.domain.repository.TokenGeneratorPort;
import com.nabgha.book.shared.security.JwtService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenGeneratorAdapter implements TokenGeneratorPort {

    private final JwtService jwtService;

    public JwtTokenGeneratorAdapter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public String generateToken(Integer userId, String email, String fullName, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("fullName", fullName);
        claims.put("roles", roles);
        return jwtService.generateTokenForClaims(claims, email);
    }
}

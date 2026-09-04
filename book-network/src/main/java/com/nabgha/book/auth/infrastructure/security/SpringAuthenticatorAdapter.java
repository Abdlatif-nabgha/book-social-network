package com.nabgha.book.auth.infrastructure.security;

import com.nabgha.book.auth.domain.repository.AuthenticatorPort;
import com.nabgha.book.user.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class SpringAuthenticatorAdapter implements AuthenticatorPort {

    private final AuthenticationManager authenticationManager;

    public SpringAuthenticatorAdapter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Integer authenticate(String email, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        UserEntity principal = (UserEntity) auth.getPrincipal();
        assert principal != null;
        return principal.getId();
    }

}

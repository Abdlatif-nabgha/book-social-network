package com.nabgha.book.auth.domain.usecase;


import com.nabgha.book.auth.domain.repository.AuthenticatorPort;
import com.nabgha.book.auth.domain.repository.TokenGeneratorPort;
import com.nabgha.book.user.domain.model.User;
import com.nabgha.book.user.domain.repository.UserRepository;

public class AuthenticateUserUseCase {

    private final AuthenticatorPort authenticatorPort;
    private final UserRepository userRepository;
    private final TokenGeneratorPort tokenGeneratorPort;

    public AuthenticateUserUseCase(AuthenticatorPort authenticatorPort, UserRepository userRepository, TokenGeneratorPort tokenGeneratorPort) {
        this.authenticatorPort = authenticatorPort;
        this.userRepository = userRepository;
        this.tokenGeneratorPort = tokenGeneratorPort;
    }

    public String execute(String email, String rawPassword) {
        Integer userId = authenticatorPort.authenticate(email, rawPassword);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return tokenGeneratorPort.generateToken(
                user.getId(), user.getEmail().getValue(), user.fullName(), user.getRoleNames()
        );
    }
}

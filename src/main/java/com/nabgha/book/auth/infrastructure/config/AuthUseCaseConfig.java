package com.nabgha.book.auth.infrastructure.config;

import com.nabgha.book.auth.domain.repository.*;
import com.nabgha.book.auth.domain.usecase.ActivateAccountUseCase;
import com.nabgha.book.auth.domain.usecase.AuthenticateUserUseCase;
import com.nabgha.book.auth.domain.usecase.RegisterUserUseCase;
import com.nabgha.book.user.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthUseCaseConfig {

    @Value("${spring.application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepository userRepository,
            PasswordEncoderPort passwordEncoderPort,
            ActivationTokenRepository activationTokenRepository,
            ActivationCodeGeneratorPort codeGeneratorPort,
            EmailSenderPort emailSenderPort
    ) {
        return new RegisterUserUseCase(
                userRepository, passwordEncoderPort, activationTokenRepository,
                codeGeneratorPort, emailSenderPort, activationUrl
        );
    }

    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase(
            AuthenticatorPort authenticatorPort,
            UserRepository userRepository,
            TokenGeneratorPort tokenGeneratorPort
    ){
        return new AuthenticateUserUseCase(authenticatorPort, userRepository, tokenGeneratorPort);
    }

    @Bean
    public ActivateAccountUseCase activateAccountUseCase(
            ActivationTokenRepository activationTokenRepository,
            UserRepository userRepository,
            ActivationCodeGeneratorPort codeGeneratorPort,
            EmailSenderPort emailSenderPort
    ){
        return new ActivateAccountUseCase(
                activationTokenRepository, userRepository,
                codeGeneratorPort, emailSenderPort, activationUrl
        );
    }

}

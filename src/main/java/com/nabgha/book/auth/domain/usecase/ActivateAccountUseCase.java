package com.nabgha.book.auth.domain.usecase;


import com.nabgha.book.auth.domain.exception.ActivationTokenExpiredException;
import com.nabgha.book.auth.domain.exception.ActivationTokenNotFoundException;
import com.nabgha.book.auth.domain.model.ActivationToken;
import com.nabgha.book.auth.domain.repository.ActivationCodeGeneratorPort;
import com.nabgha.book.auth.domain.repository.ActivationTokenRepository;
import com.nabgha.book.auth.domain.repository.EmailSenderPort;
import com.nabgha.book.user.domain.exception.UserNotFoundException;
import com.nabgha.book.user.domain.model.User;
import com.nabgha.book.user.domain.repository.UserRepository;
import jakarta.mail.MessagingException;

public class ActivateAccountUseCase {

    private final ActivationTokenRepository activationTokenRepository;
    private final UserRepository userRepository;
    private final ActivationCodeGeneratorPort codeGeneratorPort;
    private final EmailSenderPort emailSenderPort;
    private final String activationUrl;

    public ActivateAccountUseCase(ActivationTokenRepository activationTokenRepository,
                                  UserRepository userRepository,
                                  ActivationCodeGeneratorPort codeGeneratorPort,
                                  EmailSenderPort emailSenderPort,
                                  String activationUrl) {
        this.activationTokenRepository = activationTokenRepository;
        this.userRepository = userRepository;
        this.codeGeneratorPort = codeGeneratorPort;
        this.emailSenderPort = emailSenderPort;
        this.activationUrl = activationUrl;
    }

    public void execute(String code) throws MessagingException {
        ActivationToken token = activationTokenRepository.findByCode(code)
                .orElseThrow(() -> new ActivationTokenNotFoundException("Token not found"));

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (token.isExpired()) {
            String newCode = codeGeneratorPort.generate(6);
            ActivationToken newToken = ActivationToken.create(newCode, user.getId());
            activationTokenRepository.save(newToken);
            emailSenderPort.sendActivationEmail(user.getEmail().getValue(), user.fullName(), newCode, activationUrl);
            throw new ActivationTokenExpiredException("Token has expired. A new one has been sent to your email address");
        }

        user.activate();
        userRepository.save(user);
        token.validate();
        activationTokenRepository.save(token);
    }
}

package com.nabgha.book.auth.domain.usecase;


import com.nabgha.book.auth.domain.exception.EmailAlreadyExistsException;
import com.nabgha.book.auth.domain.model.ActivationToken;
import com.nabgha.book.auth.domain.repository.ActivationCodeGeneratorPort;
import com.nabgha.book.auth.domain.repository.ActivationTokenRepository;
import com.nabgha.book.auth.domain.repository.EmailSenderPort;
import com.nabgha.book.auth.domain.repository.PasswordEncoderPort;
import com.nabgha.book.user.domain.model.Email;
import com.nabgha.book.user.domain.model.User;
import com.nabgha.book.user.domain.repository.UserRepository;

public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoderPort;
    private final ActivationTokenRepository activationTokenRepository;
    private final ActivationCodeGeneratorPort codeGeneratorPort;
    private final EmailSenderPort emailSenderPort;
    private final String activationUrl;

    public RegisterUserUseCase(UserRepository userRepository,
                               PasswordEncoderPort passwordEncoderPort,
                               ActivationTokenRepository activationTokenRepository,
                               ActivationCodeGeneratorPort codeGeneratorPort,
                               EmailSenderPort emailSenderPort,
                               String activationUrl) {
        this.userRepository = userRepository;
        this.passwordEncoderPort = passwordEncoderPort;
        this.activationTokenRepository = activationTokenRepository;
        this.codeGeneratorPort = codeGeneratorPort;
        this.emailSenderPort = emailSenderPort;
        this.activationUrl = activationUrl;
    }

    public User execute(String firstName, String lastName, String rawEmail, String rawPassword) throws jakarta.mail.MessagingException {
        Email email = new Email(rawEmail);

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        String encodedPassword = passwordEncoderPort.encode(rawPassword);
        User user = User.register(firstName, lastName, email, encodedPassword);
        User savedUser = userRepository.save(user);

        String code = codeGeneratorPort.generate(6);
        ActivationToken token = ActivationToken.create(code, savedUser.getId());
        activationTokenRepository.save(token);
        emailSenderPort.sendActivationEmail(savedUser.getEmail().getValue(), savedUser.fullName(), code, activationUrl);

        return savedUser;
    }
}

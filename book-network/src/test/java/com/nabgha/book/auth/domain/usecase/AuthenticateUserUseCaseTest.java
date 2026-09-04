package com.nabgha.book.auth.domain.usecase;

import com.nabgha.book.auth.domain.repository.AuthenticatorPort;
import com.nabgha.book.auth.domain.repository.TokenGeneratorPort;
import com.nabgha.book.user.domain.model.Email;
import com.nabgha.book.user.domain.model.User;
import com.nabgha.book.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserUseCaseTest {

    @Mock
    private AuthenticatorPort authenticatorPort;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenGeneratorPort tokenGeneratorPort;

    private AuthenticateUserUseCase authenticateUserUseCase;

    @BeforeEach
    void setUp() {
        authenticateUserUseCase = new AuthenticateUserUseCase(authenticatorPort, userRepository, tokenGeneratorPort);
    }

    @Test
    @DisplayName("Should authenticate user and generate valid JWT token")
    void shouldAuthenticateAndGenerateToken() {
        String email = "test@example.com";
        String password = "password123";
        Integer userId = 1;

        User user = User.reconstitute(
                userId, "Abd Latif", "Nabgha", LocalDate.of(1995, 1, 1),
                new Email(email), "encodedPass", true, false,
                List.of("USER", "ADMIN"), LocalDateTime.now()
        );

        when(authenticatorPort.authenticate(email, password)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tokenGeneratorPort.generateToken(userId, email, "Abd Latif Nabgha", List.of("USER", "ADMIN")))
                .thenReturn("mock-jwt-token");

        String token = authenticateUserUseCase.execute(email, password);

        assertThat(token).isEqualTo("mock-jwt-token");
        verify(authenticatorPort, times(1)).authenticate(email, password);
        verify(userRepository, times(1)).findById(userId);
        verify(tokenGeneratorPort, times(1)).generateToken(userId, email, "Abd Latif Nabgha", List.of("USER", "ADMIN"));
    }
}

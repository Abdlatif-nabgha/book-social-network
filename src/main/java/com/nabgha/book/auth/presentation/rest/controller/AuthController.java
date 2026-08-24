package com.nabgha.book.auth.presentation.rest.controller;

import com.nabgha.book.auth.domain.usecase.ActivateAccountUseCase;
import com.nabgha.book.auth.domain.usecase.AuthenticateUserUseCase;
import com.nabgha.book.auth.domain.usecase.RegisterUserUseCase;
import com.nabgha.book.auth.presentation.rest.dto.AuthenticationRequest;
import com.nabgha.book.auth.presentation.rest.dto.AuthenticationResponse;
import com.nabgha.book.auth.presentation.rest.dto.RegisterRequest;
import com.nabgha.book.auth.presentation.rest.dto.RegisterResponse;
import com.nabgha.book.auth.presentation.rest.mapper.AuthDtoMapper;
import com.nabgha.book.common.presentation.ApiResponse;
import com.nabgha.book.user.domain.model.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final ActivateAccountUseCase activateAccountUseCase;
    private final AuthDtoMapper authDtoMapper;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) throws jakarta.mail.MessagingException {
        User user = registerUserUseCase.execute(
                request.firstName(), request.lastName(), request.email(), request.password()
        );

        RegisterResponse response = authDtoMapper.toRegisterResponse(user);

        URI location = URI.create("/users/" + response.id());

        return ResponseEntity.created(location)
                .body(ApiResponse.of("User registered successfully. Check your email to activate your account.", response));

    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        String token = authenticateUserUseCase.execute(request.email(), request.password());
        return ResponseEntity.ok(ApiResponse.of("Authenticated successfully",
                AuthenticationResponse.builder().token(token).build()));
    }

    @GetMapping("/activate-account")
    public ResponseEntity<ApiResponse<Void>> confirm(@RequestParam String token) throws Exception {
        activateAccountUseCase.execute(token);
        return ResponseEntity.ok(ApiResponse.of("Account activated successfully", null));
    }

}

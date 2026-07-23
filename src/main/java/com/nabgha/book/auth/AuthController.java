package com.nabgha.book.auth;


import com.nabgha.book.common.ApiResponse;
import com.nabgha.book.user.UserDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(
            @RequestBody @Valid RegisterRequest request
    ) throws MessagingException {
        UserDto user = service.register(request);
        URI location = URI.create("/api/v1/auth/register" + user.id());
        return ResponseEntity
                .created(location)
                .body(ApiResponse.of("User registered successfully", user));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.of("Authenticated successfully", service.authenticate(request)));
    }

    @GetMapping("/activate-account")
    public ResponseEntity<ApiResponse<Void>> confirm(@RequestParam String token) throws MessagingException {
        service.activateAccount(token);
        return ResponseEntity
                .ok(ApiResponse.of("Account activated successfully", null));
    }
}

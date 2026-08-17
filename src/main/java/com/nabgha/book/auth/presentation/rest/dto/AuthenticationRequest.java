package com.nabgha.book.auth.presentation.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationRequest(
        @Email(message = "email is not formatted correctly")
        @NotBlank(message = "email is mandatory")
        String email,

        @NotBlank(message = "password is mandatory")
        @Size(min = 8, message = "password should be 8 characters minimum")
        String password
) {}
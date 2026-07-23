package com.nabgha.book.auth;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record AuthenticationRequest(
        @Email(message = "Email format is required")
        @NotEmpty(message = "Email is required")
        @NotBlank(message = "Email is mandatory")
        String email,
        @NotEmpty(message = "Password is required")
        @NotBlank(message = "Password is mandatory")
        @Size(min = 6, max =  12, message = "Password length is between 6 and 12")
        String password
) {

}

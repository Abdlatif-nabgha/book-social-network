package com.nabgha.book.user.presentation.rest.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record UpdateUserProfileRequest(
        @NotBlank(message = "First name is mandatory")
        String firstName,
        @NotBlank(message = "Last name is mandatory")
        String lastName,
        LocalDate birthDate
) {
}

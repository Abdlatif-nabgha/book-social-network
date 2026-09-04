package com.nabgha.book.user.presentation.rest.dto;

import java.time.LocalDate;
import java.util.List;

public record UserProfileResponse(
        Integer id,
        String firstName,
        String lastName,
        String fullName,
        String email,
        LocalDate birthDate,
        List<String> roles,
        String token
) {
}

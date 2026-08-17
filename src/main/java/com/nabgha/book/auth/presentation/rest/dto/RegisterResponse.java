package com.nabgha.book.auth.presentation.rest.dto;

import lombok.Builder;

@Builder
public record RegisterResponse(
        Integer id,
        String firstName,
        String lastName,
        String email
) {}
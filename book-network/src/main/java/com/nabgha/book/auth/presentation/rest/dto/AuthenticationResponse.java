package com.nabgha.book.auth.presentation.rest.dto;

import lombok.Builder;

@Builder
public record AuthenticationResponse(
        String token
) {}
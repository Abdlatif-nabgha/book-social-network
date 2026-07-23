package com.nabgha.book.auth;

import lombok.Builder;

@Builder
public record AuthenticationResponse(
        String token
) {
}

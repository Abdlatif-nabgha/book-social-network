package com.nabgha.book.user;


import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserDto(
        Integer id,
        String firstName,
        String lastName,
        String email,
        boolean enabled,
        LocalDateTime createdAt
) {
}

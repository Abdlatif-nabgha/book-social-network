package com.nabgha.book.user;


import com.nabgha.book.role.Role;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record UserDto(
        Integer id,
        String firstName,
        String lastName,
        String email,
        List<String> roles,
        boolean enabled,
        LocalDateTime createdAt
) {
}

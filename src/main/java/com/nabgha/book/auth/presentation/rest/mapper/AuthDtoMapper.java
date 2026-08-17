package com.nabgha.book.auth.presentation.rest.mapper;

import com.nabgha.book.auth.presentation.rest.dto.RegisterResponse;
import com.nabgha.book.user.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class AuthDtoMapper {

    public RegisterResponse toRegisterResponse(User user) {
        return RegisterResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail().getValue())
                .build();
    }
}
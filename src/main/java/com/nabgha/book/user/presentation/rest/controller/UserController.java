package com.nabgha.book.user.presentation.rest.controller;

import com.nabgha.book.auth.domain.repository.TokenGeneratorPort;
import com.nabgha.book.common.presentation.ApiResponse;
import com.nabgha.book.role.infrastructure.persistence.jpa.entity.RoleEntity;
import com.nabgha.book.user.infrastructure.persistence.jpa.entity.UserEntity;
import com.nabgha.book.user.infrastructure.persistence.jpa.repository.UserJpaRepository;
import com.nabgha.book.user.presentation.rest.dto.UpdateUserProfileRequest;
import com.nabgha.book.user.presentation.rest.dto.UserProfileResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "User Profile")
@RequiredArgsConstructor
public class UserController {

    private final UserJpaRepository userJpaRepository;
    private final TokenGeneratorPort tokenGeneratorPort;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUserProfile(
            @AuthenticationPrincipal UserEntity connectedUser
    ) {
        UserEntity user = userJpaRepository.findById(connectedUser.getId())
                .orElse(connectedUser);

        List<String> roles = user.getRoles() != null
                ? user.getRoles().stream().map(RoleEntity::getName).toList()
                : Collections.emptyList();

        UserProfileResponse response = new UserProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.fullName(),
                user.getEmail(),
                user.getBirthDate(),
                roles,
                null
        );

        return ResponseEntity.ok(ApiResponse.of("User profile retrieved", response));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateCurrentUserProfile(
            @Valid @RequestBody UpdateUserProfileRequest request,
            @AuthenticationPrincipal UserEntity connectedUser
    ) {
        UserEntity user = userJpaRepository.findById(connectedUser.getId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        if (request.birthDate() != null) {
            user.setBirthDate(request.birthDate());
        }

        UserEntity savedUser = userJpaRepository.save(user);

        List<String> roles = savedUser.getRoles() != null
                ? savedUser.getRoles().stream().map(RoleEntity::getName).toList()
                : Collections.emptyList();

        String newToken = tokenGeneratorPort.generateToken(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.fullName(),
                roles
        );

        UserProfileResponse response = new UserProfileResponse(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.fullName(),
                savedUser.getEmail(),
                savedUser.getBirthDate(),
                roles,
                newToken
        );

        return ResponseEntity.ok(ApiResponse.of("User profile updated successfully", response));
    }
}

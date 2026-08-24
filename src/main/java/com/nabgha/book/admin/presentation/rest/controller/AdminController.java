package com.nabgha.book.admin.presentation.rest.controller;

import com.nabgha.book.admin.presentation.rest.dto.AdminStatsResponse;
import com.nabgha.book.book.domain.repository.BookRepository;
import com.nabgha.book.common.presentation.ApiResponse;
import com.nabgha.book.user.domain.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AdminStatsResponse> getStats() {
        long totalUsers = userRepository.countAll();
        long totalBooks = bookRepository.countAll();

        AdminStatsResponse stats = AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalBooks(totalBooks)
                .build();
        return ApiResponse.of("Stats retrieved successfully", stats);
    }
}

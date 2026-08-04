package com.nabgha.book.admin;

import com.nabgha.book.book.BookRepository;
import com.nabgha.book.common.ApiResponse;
import com.nabgha.book.user.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin")
public class AdminController {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<AdminStatsResponse> getStatistics() {
        AdminStatsResponse statistics = AdminStatsResponse.builder()
                .totalUsers(userRepository.count())
                .totalBooks(bookRepository.count())
                .build();

        return ApiResponse.of("Statistics", statistics);
    }
}

package com.nabgha.book.admin.presentation.rest.dto;

import lombok.Builder;

@Builder
public record AdminStatsResponse(
        long totalUsers,
        long totalBooks
) {}
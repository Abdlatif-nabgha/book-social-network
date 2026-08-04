package com.nabgha.book.admin;

import lombok.Builder;

@Builder
public record AdminStatsResponse(
        long totalUsers,
        long totalBooks
) {}
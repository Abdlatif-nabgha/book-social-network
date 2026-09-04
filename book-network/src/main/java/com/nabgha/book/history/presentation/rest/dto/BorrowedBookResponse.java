package com.nabgha.book.history.presentation.rest.dto;

import lombok.Builder;

@Builder
public record BorrowedBookResponse(
        Integer id,
        Integer bookId,
        String title,
        String author,
        String isbn,
        double rate,
        boolean returned,
        boolean returnedApproved
) {}

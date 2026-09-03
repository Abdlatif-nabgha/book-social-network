package com.nabgha.book.book.presentation.rest.dto;

import lombok.Builder;

@Builder
public record BookResponse(
        Integer id,
        String title,
        String author,
        String isbn,
        String synopsis,
        boolean shareable,
        boolean archived,
        Integer ownerId,
        String ownerName,
        byte[] bookCover,
        double rate
) {}
package com.nabgha.book.book.presentation.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record BookRequest(
        @NotBlank(message = "100") String title,
        @NotBlank(message = "101") String author,
        @NotBlank(message = "102") String isbn,
        @NotBlank(message = "103") String synopsis,
        boolean shareable
) {}
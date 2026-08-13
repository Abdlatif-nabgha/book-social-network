package com.nabgha.book.common;

import lombok.Builder;

@Builder
public record ApiResponse <T> (
        String message,
        T data
) {
    public static <T> ApiResponse <T> of(String message, T data) {
        return new ApiResponse<T>(message, data);
    }
}

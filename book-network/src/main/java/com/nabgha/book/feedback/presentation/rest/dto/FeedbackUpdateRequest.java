package com.nabgha.book.feedback.presentation.rest.dto;

public record FeedbackUpdateRequest(
        Double note,
        String comment
) {}
package com.nabgha.book.feedback.presentation.rest.dto;

import lombok.Builder;

@Builder
public record FeedbackResponse(
        Integer id,
        Double note,
        String comment,
        Integer bookId,
        boolean ownFeedback
) {}
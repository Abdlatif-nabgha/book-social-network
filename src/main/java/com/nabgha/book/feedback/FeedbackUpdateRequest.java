package com.nabgha.book.feedback;


public record FeedbackUpdateRequest(
        Double note,      // nullable — only update if present
        String comment    // nullable — only update if present
) {}

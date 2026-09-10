package com.nabgha.book.feedback.domain.event;

public record FeedbackCreatedEvent(
        Integer bookId,
        Double note,
        String comment,
        Integer userId
) {
}

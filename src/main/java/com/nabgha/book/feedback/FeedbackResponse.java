package com.nabgha.book.feedback;


import lombok.Builder;

@Builder
public record FeedbackResponse(
        Integer id,
        Double note,
        String comment,
        Integer bookId
) {
}

package com.nabgha.book.feedback;

import com.nabgha.book.book.Book;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {

    public Feedback toFeedback(FeedbackRequest request) {
        return Feedback.builder()
                .note(request.note())
                .comment(request.comment())
                .build();
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .comment(feedback.getComment())
                .note(feedback.getNote())
                .bookId(feedback.getBook().getId())
                .build();
    }
}

package com.nabgha.book.book.domain.service;


import com.nabgha.book.feedback.domain.model.Feedback;
import com.nabgha.book.feedback.domain.repository.FeedbackRepository;

import java.util.List;

public class BookRatingService {

    private final FeedbackRepository feedbackRepository;

    public BookRatingService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public double calculateAverageRating(Integer bookId) {
        List<Feedback> feedbacks = feedbackRepository.findAllByBookId(bookId);
        if (feedbacks.isEmpty()) {
            return 0.0;
        }
        double avg = feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);

        return Math.round(avg * 10.0) / 10.0;
    }
}

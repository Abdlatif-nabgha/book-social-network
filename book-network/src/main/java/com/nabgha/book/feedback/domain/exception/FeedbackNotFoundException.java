package com.nabgha.book.feedback.domain.exception;

public class FeedbackNotFoundException extends RuntimeException {
    public FeedbackNotFoundException(Integer id) {
        super("No feedback found with id: " + id);
    }
}
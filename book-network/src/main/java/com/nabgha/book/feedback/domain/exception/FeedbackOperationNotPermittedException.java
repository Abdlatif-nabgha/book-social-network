package com.nabgha.book.feedback.domain.exception;

public class FeedbackOperationNotPermittedException extends RuntimeException {
    public FeedbackOperationNotPermittedException(String message) {
        super(message);
    }
}
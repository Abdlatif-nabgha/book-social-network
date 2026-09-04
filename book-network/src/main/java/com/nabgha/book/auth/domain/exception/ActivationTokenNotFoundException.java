package com.nabgha.book.auth.domain.exception;


public class ActivationTokenNotFoundException extends RuntimeException {
    public ActivationTokenNotFoundException(String message) {
        super(message);
    }
}

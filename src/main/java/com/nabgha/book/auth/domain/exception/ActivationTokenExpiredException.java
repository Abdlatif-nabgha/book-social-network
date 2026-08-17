package com.nabgha.book.auth.domain.exception;

/**
 * @auther abdlatif-nabgha
 **/
public class ActivationTokenExpiredException extends RuntimeException {
    public ActivationTokenExpiredException(String message) {
        super(message);
    }
}

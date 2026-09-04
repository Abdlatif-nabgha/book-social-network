package com.nabgha.book.book.domain.exception;


public class BookOperationNotPermittedException extends RuntimeException {
    public BookOperationNotPermittedException(String message) {
        super(message);
    }
}

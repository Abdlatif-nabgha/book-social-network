package com.nabgha.book.book.domain.exception;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Integer bookId) {
        super("No book found with id: " + bookId);
    }
}
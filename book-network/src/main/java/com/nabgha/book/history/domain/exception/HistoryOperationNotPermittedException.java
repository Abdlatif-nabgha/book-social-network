package com.nabgha.book.history.domain.exception;

public class HistoryOperationNotPermittedException extends RuntimeException {
    public HistoryOperationNotPermittedException(String message) { super(message); }
}
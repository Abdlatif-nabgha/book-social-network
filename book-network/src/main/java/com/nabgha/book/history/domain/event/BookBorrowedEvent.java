package com.nabgha.book.history.domain.event;

public record BookBorrowedEvent(
        Integer bookId,
        Integer userId
) {
}

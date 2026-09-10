package com.nabgha.book.history.domain.event;

public record BookReturnedEvent(
        Integer bookId,
        Integer userId
) {
}

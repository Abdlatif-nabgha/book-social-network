package com.nabgha.book.history.infrastructure.persistence.jpa.mapper;

import com.nabgha.book.book.infrastructure.persistence.jpa.entity.BookEntity;
import com.nabgha.book.history.domain.model.BookTransactionHistory;
import com.nabgha.book.history.infrastructure.persistence.jpa.entity.BookTransactionHistoryEntity;
import com.nabgha.book.user.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class BookTransactionHistoryJpaMapper {

    public BookTransactionHistory toDomain(BookTransactionHistoryEntity entity) {
        return BookTransactionHistory.reconstitute(
                entity.getId(),
                entity.getBook().getId(),
                entity.getUser().getId(),
                entity.isReturned(),
                entity.isReturnedApproved(),
                entity.getCreationDate()
        );
    }

    public BookTransactionHistoryEntity toEntity(BookTransactionHistory history, BookEntity book, UserEntity user) {
        return BookTransactionHistoryEntity.builder()
                .id(history.getId())
                .returned(history.isReturned())
                .returnedApproved(history.isReturnedApproved())
                .book(book)
                .user(user)
                .build();
    }
}
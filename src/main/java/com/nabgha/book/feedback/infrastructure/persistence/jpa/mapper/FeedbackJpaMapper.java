package com.nabgha.book.feedback.infrastructure.persistence.jpa.mapper;

import com.nabgha.book.book.infrastructure.persistence.jpa.entity.BookEntity;
import com.nabgha.book.feedback.domain.model.Feedback;
import com.nabgha.book.feedback.infrastructure.persistence.jpa.entity.FeedbackEntity;
import org.springframework.stereotype.Component;

@Component
public class FeedbackJpaMapper {

    public Feedback toDomain(FeedbackEntity entity) {
        return Feedback.reconstitute(
                entity.getId(),
                entity.getNote(),
                entity.getComment(),
                entity.getBook().getId(),
                entity.getCreatedBy(),
                entity.getCreationDate()
        );
    }

    public FeedbackEntity toEntity(Feedback feedback, BookEntity book) {
        return FeedbackEntity.builder()
                .id(feedback.getId())
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .book(book)
                .build();
    }
}
package com.nabgha.book.feedback.infrastructure.persistence.jpa.entity;

import com.nabgha.book.book.infrastructure.persistence.jpa.entity.BookEntity;
import com.nabgha.book.common.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "feedback")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class FeedbackEntity extends BaseEntity {

    private Double note;
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;
}
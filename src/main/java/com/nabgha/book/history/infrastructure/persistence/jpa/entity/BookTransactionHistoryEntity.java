package com.nabgha.book.history.infrastructure.persistence.jpa.entity;

import com.nabgha.book.book.infrastructure.persistence.jpa.entity.BookEntity;
import com.nabgha.book.common.infrastructure.persistence.BaseEntity;
import com.nabgha.book.user.infrastructure.persistence.jpa.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "book_transaction_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class BookTransactionHistoryEntity extends BaseEntity {

    private boolean returned;
    private boolean returnedApproved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
package com.nabgha.book.book.infrastructure.persistence.jpa.entity;


import com.nabgha.book.common.infrastructure.persistence.BaseEntity;
import com.nabgha.book.feedback.infrastructure.persistence.jpa.entity.FeedbackEntity;
import com.nabgha.book.user.infrastructure.persistence.jpa.entity.UserEntity;
import com.nabgha.book.history.infrastructure.persistence.jpa.entity.BookTransactionHistoryEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BookEntity extends BaseEntity {

    private String title;
    private String author;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedbackEntity> feedbacks = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistoryEntity> histories = new ArrayList<>();

}

package com.nabgha.book.book;


import com.nabgha.book.common.BaseEntity;
import com.nabgha.book.feedback.Feedback;
import com.nabgha.book.history.BookTransactionHistory;
import com.nabgha.book.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Setter @Getter @NoArgsConstructor @AllArgsConstructor @SuperBuilder @ToString
public class Book extends BaseEntity {

    private String title;
    private String author;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;

    @ManyToOne
    @JoinColumn(name = "owner_id") // optional: hibernate does it by default
    @ToString.Exclude
    private User owner;

    @OneToMany(mappedBy = "book")
    @ToString.Exclude
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "book")
    @ToString.Exclude
    private List<BookTransactionHistory> histories;
}

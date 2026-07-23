package com.nabgha.book.feedback;

import com.nabgha.book.book.Book;
import com.nabgha.book.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor
@SuperBuilder
@ToString
public class Feedback extends BaseEntity {

    private Double note;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "book_id")
    @ToString.Exclude
    private Book book;

}

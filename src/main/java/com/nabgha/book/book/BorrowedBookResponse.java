package com.nabgha.book.book;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowedBookResponse {
    private Integer id;
    private String title;
    private String author;
    private String isbn;
    private double rate;
    private boolean returned;
    private boolean returnedApproved;
}

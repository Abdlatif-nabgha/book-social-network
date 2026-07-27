package com.nabgha.book.book;

import com.nabgha.book.history.BookTransactionHistory;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {


    public Book toBook(BookRequest request) {
        return Book.builder()
                .title(request.title())
                .isbn(request.isbn())
                .author(request.author())
                .synopsis(request.synopsis())
                .archived(false)
                .shareable(request.shareable())
                .build();
    }

    public BookResponse toBookDto(Book request) {
        return BookResponse.builder()
                .id(request.getId())
                .title(request.getTitle())
                .isbn(request.getIsbn())
                .author(request.getAuthor())
                .synopsis(request.getSynopsis())
                .archived(request.isArchived())
                .shareable(request.isShareable())
                .bookCover(request.getBookCover())
                .rate(request.getRate())
                .ownerId(request.getOwner().getId())
                .owner(request.getOwner().fullName())
                .build();
    }

    public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history) {
        return BorrowedBookResponse.builder()
                .id(history.getBook().getId())
                .title(history.getBook().getTitle())
                .isbn(history.getBook().getIsbn())
                .author(history.getBook().getAuthor())
                .rate(history.getBook().getRate())
                .returned(history.isReturned())
                .returnedApproved(history.isReturnedApproved())
                .build();
    }
}

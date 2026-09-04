package com.nabgha.book.history.presentation.rest.mapper;

import com.nabgha.book.book.domain.exception.BookNotFoundException;
import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.domain.repository.BookRepository;
import com.nabgha.book.book.domain.service.BookRatingService;
import com.nabgha.book.common.domain.PageResult;
import com.nabgha.book.common.presentation.PageResponse;
import com.nabgha.book.history.domain.model.BookTransactionHistory;
import com.nabgha.book.history.presentation.rest.dto.BorrowedBookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HistoryDtoMapper {

    private final BookRepository bookRepository;
    private final BookRatingService bookRatingService;

    public BorrowedBookResponse toResponse(BookTransactionHistory history) {
        Book book = bookRepository.findById(history.getBookId())
                .orElseThrow(() -> new BookNotFoundException(history.getBookId()));

        return BorrowedBookResponse.builder()
                .id(history.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .bookId(book.getId())
                .rate(bookRatingService.calculateAverageRating(book.getId()))
                .returned(history.isReturned())
                .returnedApproved(history.isReturnedApproved())
                .build();
    }

    public PageResponse<BorrowedBookResponse> toPageResponse(PageResult<BookTransactionHistory> result) {
        return new PageResponse<>(
                result.content().stream().map(this::toResponse).toList(),
                result.pageNumber(),
                result.pageSize(),
                result.totalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }
}

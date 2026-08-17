package com.nabgha.book.book.presentation.rest.mapper;

import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.domain.service.BookRatingService;
import com.nabgha.book.book.presentation.rest.dto.BookResponse;
import com.nabgha.book.common.domain.PageResult;
import com.nabgha.book.common.presentation.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookDtoMapper {

    private final BookRatingService bookRatingService;

    public BookResponse toResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .shareable(book.isShareable())
                .archived(book.isArchived())
                .ownerId(book.getOwnerId())
                .ownerName(book.getOwnerName())
                .bookCover(book.getBookCover())
                .rate(bookRatingService.calculateAverageRating(book.getId()))
                .build();
    }


    public PageResponse<BookResponse> toPageResponse(PageResult<Book> result) {
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

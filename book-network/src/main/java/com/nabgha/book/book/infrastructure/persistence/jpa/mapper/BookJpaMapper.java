package com.nabgha.book.book.infrastructure.persistence.jpa.mapper;

import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.infrastructure.persistence.jpa.entity.BookEntity;
import com.nabgha.book.user.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookJpaMapper {

    public Book toDomain(BookEntity book) {
        // TODO: wire real feedback mapping once the feedback module is migrated
        return Book.reconstitute(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getSynopsis(),
                book.getBookCover(),
                book.isArchived(),
                book.isShareable(),
                book.getOwner().getId(),
                book.getOwner().fullName(),
                book.getCreationDate()
        );
    }

    public BookEntity toEntity(Book book, UserEntity owner) {
        return BookEntity.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .bookCover(book.getBookCover())
                .archived(book.isArchived())
                .shareable(book.isShareable())
                .owner(owner)
                .build();
    }

}

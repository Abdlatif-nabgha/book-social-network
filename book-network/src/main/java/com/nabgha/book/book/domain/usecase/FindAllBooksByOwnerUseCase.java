package com.nabgha.book.book.domain.usecase;


import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.domain.repository.BookRepository;
import com.nabgha.book.common.domain.PageResult;

import java.util.List;

public class FindAllBooksByOwnerUseCase {

    private final BookRepository bookRepository;

    public FindAllBooksByOwnerUseCase(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public PageResult<Book> execute(int page, int size, Integer ownerId) {
        List<Book> books = bookRepository.findAllByOwner(page, size, ownerId);
        long total = bookRepository.countByOwner(ownerId);
        return new PageResult<>(books, page, size, total);
    }
}

package com.nabgha.book.book.domain.usecase;


import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.domain.repository.BookRepository;
import com.nabgha.book.common.domain.PageResult;

import java.util.List;

public class FindAllDisplayableBooksUseCase {

    private final BookRepository bookRepository;
    public FindAllDisplayableBooksUseCase(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public PageResult<Book> execute(int page, int size, Integer connectedUserId) {
        List<Book> books = bookRepository.findAllDisplayableBooks(page, size, connectedUserId);
        long total = bookRepository.countDisplayableBooks(connectedUserId);
        return new PageResult<>(books, page, size, total);
    }
}

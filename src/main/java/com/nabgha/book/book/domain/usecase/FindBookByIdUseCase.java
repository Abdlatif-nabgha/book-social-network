package com.nabgha.book.book.domain.usecase;


import com.nabgha.book.book.domain.exception.BookNotFoundException;
import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.domain.repository.BookRepository;

public class FindBookByIdUseCase {

    private final BookRepository bookRepository;

    public FindBookByIdUseCase(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book execute(Integer bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
    }
}

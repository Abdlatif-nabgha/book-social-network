package com.nabgha.book.book.domain.usecase;


import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.domain.repository.BookRepository;

public class CreateBookUseCase {

    private final BookRepository bookRepository;

    public CreateBookUseCase(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book execute(String title, String author, String isbn, String synopsis,
                        Integer ownerId, String ownerName) {
        Book book = Book.create(title, author, isbn, synopsis, ownerId, ownerName);
        return bookRepository.save(book);
    }
}

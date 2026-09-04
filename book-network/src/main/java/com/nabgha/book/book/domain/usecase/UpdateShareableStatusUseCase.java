package com.nabgha.book.book.domain.usecase;


import com.nabgha.book.book.domain.exception.BookNotFoundException;
import com.nabgha.book.book.domain.exception.BookOperationNotPermittedException;
import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.domain.repository.BookRepository;

public class UpdateShareableStatusUseCase {

    private final BookRepository bookRepository;

    public UpdateShareableStatusUseCase(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book execute(Integer bookId, Integer connectedUserId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        if (!book.isOwnedBy(connectedUserId)) {
            throw new BookOperationNotPermittedException(
                    "You cannot update the shareable status of a book you don't own"
            );
        }
        book.toggleShareable();
        return bookRepository.save(book);
    }
}

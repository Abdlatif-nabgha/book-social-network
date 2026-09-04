package com.nabgha.book.book.domain.usecase;

import com.nabgha.book.book.domain.exception.BookNotFoundException;
import com.nabgha.book.book.domain.exception.BookOperationNotPermittedException;
import com.nabgha.book.book.domain.repository.BookRepository;
import com.nabgha.book.history.domain.repository.BookTransactionHistoryRepository;

public class DeleteBookUseCase {
    
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;

    public DeleteBookUseCase(BookRepository bookRepository, BookTransactionHistoryRepository history) {
        this.bookRepository = bookRepository;
        this.bookTransactionHistoryRepository = history;
    }

    public void execute(Integer bookId, Integer userId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        if (!book.isOwnedBy(userId)) {
            throw new BookOperationNotPermittedException(
                "You cannot delete a book you don't own"
            );
        }

        if (bookTransactionHistoryRepository.isBookCurrentlyBorrowed(bookId)) {
            throw new BookOperationNotPermittedException(
                "You cannot delete a book that is currently borrowed"
            );
        }
        
        bookRepository.deleteById(bookId);
        
    }    
}

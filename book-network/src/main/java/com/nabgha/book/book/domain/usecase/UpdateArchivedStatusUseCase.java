package com.nabgha.book.book.domain.usecase;

import com.nabgha.book.book.domain.exception.BookNotFoundException;
import com.nabgha.book.book.domain.exception.BookOperationNotPermittedException;
import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.domain.repository.BookRepository;

public class UpdateArchivedStatusUseCase {
    
    private final BookRepository bookRepository;

    public UpdateArchivedStatusUseCase(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book execute(Integer bookId, Integer userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        
        if (!book.isOwnedBy(userId)) {
            throw new BookOperationNotPermittedException(
                "You cannot update the archive status of a book you don't own"
            );
        }
        book.toggleArchived();
        return bookRepository.save(book);
    }
}

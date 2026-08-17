package com.nabgha.book.history.domain.usecase;


import com.nabgha.book.book.domain.exception.BookNotFoundException;
import com.nabgha.book.book.domain.exception.BookOperationNotPermittedException;
import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.domain.repository.BookRepository;
import com.nabgha.book.history.domain.model.BookTransactionHistory;
import com.nabgha.book.history.domain.repository.BookTransactionHistoryRepository;

public class BorrowBookUseCase {

    private final BookTransactionHistoryRepository historyRepository;
    private final BookRepository bookRepository;

    public BorrowBookUseCase(BookTransactionHistoryRepository historyRepository, BookRepository bookRepository) {
        this.historyRepository = historyRepository;
        this.bookRepository = bookRepository;
    }

    public BookTransactionHistory execute(Integer bookId, Integer userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        if (book.isAvailableForBorrowing()) {
            throw new BookOperationNotPermittedException("This book is not shareable");
        }
        if (book.isOwnedBy(userId)) {
            throw new BookOperationNotPermittedException("You cannot borrow your own book");
        }
        if (historyRepository.isBookCurrentlyBorrowed(bookId)) {
            throw new BookOperationNotPermittedException("This book is already borrowed");
        }

        BookTransactionHistory history = BookTransactionHistory.create(bookId, userId);
        return historyRepository.save(history);
    }
}

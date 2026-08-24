package com.nabgha.book.history.domain.usecase;


import com.nabgha.book.book.domain.exception.BookNotFoundException;
import com.nabgha.book.book.domain.exception.BookOperationNotPermittedException;
import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.domain.repository.BookRepository;
import com.nabgha.book.history.domain.exception.BookTransactionHistoryNotFoundException;
import com.nabgha.book.history.domain.model.BookTransactionHistory;
import com.nabgha.book.history.domain.repository.BookTransactionHistoryRepository;

public class ApproveReturnBookUseCase {

    private final BookTransactionHistoryRepository historyRepository;
    private final BookRepository bookRepository;

    public ApproveReturnBookUseCase(BookTransactionHistoryRepository historyRepository, BookRepository bookRepository) {
        this.historyRepository = historyRepository;
        this.bookRepository = bookRepository;
    }

    public BookTransactionHistory execute(Integer bookId, Integer userId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        if (!book.isOwnedBy(userId)) {
            throw new BookOperationNotPermittedException("You cannot approve the return of a book you don't own");
        }
        BookTransactionHistory history = historyRepository.findReturnedNotApprovedByBookAndOwner(bookId, userId)
                .orElseThrow(() -> new BookTransactionHistoryNotFoundException(
                        "The book is not returned yet, you cannot approve its return"
                ));
        history.approveReturned();
        return historyRepository.save(history);
    }
}

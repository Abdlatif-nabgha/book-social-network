package com.nabgha.book.history.domain.repository;


import com.nabgha.book.history.domain.model.BookTransactionHistory;

import java.util.List;
import java.util.Optional;

public interface BookTransactionHistoryRepository {

    BookTransactionHistory save(BookTransactionHistory bookTransactionHistory);
    List<BookTransactionHistory> findAllBorrowedByUser(int page, int size, Integer userId);
    long countBorrowedByUser(Integer userId);
    List<BookTransactionHistory> findAllReturnedByOwner(int page, int size, Integer userId);
    long countReturnedByOwner(Integer userId);
    boolean isBookCurrentlyBorrowed(Integer bookId);
    Optional<BookTransactionHistory> findActiveBorrowByBookAndUser(Integer bookId, Integer userId);
    Optional<BookTransactionHistory> findReturnedNotApprovedByBookAndOwner(Integer bookId, Integer ownerId);
}

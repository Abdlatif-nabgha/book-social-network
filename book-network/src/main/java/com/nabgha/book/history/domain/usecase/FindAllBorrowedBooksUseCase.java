package com.nabgha.book.history.domain.usecase;


import com.nabgha.book.common.domain.PageResult;
import com.nabgha.book.history.domain.model.BookTransactionHistory;
import com.nabgha.book.history.domain.repository.BookTransactionHistoryRepository;

import java.util.List;

public class FindAllBorrowedBooksUseCase {

    private final BookTransactionHistoryRepository historyRepository;
    public FindAllBorrowedBooksUseCase(BookTransactionHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public PageResult<BookTransactionHistory> execute(int page, int size, Integer connectedUserId) {
        List<BookTransactionHistory> histories = historyRepository.findAllBorrowedByUser(page, size, connectedUserId);
        long total = historyRepository.countBorrowedByUser(connectedUserId);
        return new PageResult<>(histories, page, size, total);
    }
}

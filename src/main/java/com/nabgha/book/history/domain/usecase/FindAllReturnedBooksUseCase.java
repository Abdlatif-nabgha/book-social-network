package com.nabgha.book.history.domain.usecase;

import com.nabgha.book.common.domain.PageResult;
import com.nabgha.book.history.domain.model.BookTransactionHistory;
import com.nabgha.book.history.domain.repository.BookTransactionHistoryRepository;

import java.util.List;



public class FindAllReturnedBooksUseCase {

    private final BookTransactionHistoryRepository historyRepository;
    public FindAllReturnedBooksUseCase(BookTransactionHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public PageResult<BookTransactionHistory> execute(int page, int size, int userId) {
        List<BookTransactionHistory> histories = historyRepository.findAllReturnedByOwner(page, size, userId);
        long total = historyRepository.countReturnedByOwner(userId);
        return new PageResult<>(histories, page, size, total);
    }
}

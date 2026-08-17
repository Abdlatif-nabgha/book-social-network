package com.nabgha.book.history.infrastructure.config;


import com.nabgha.book.book.domain.repository.BookRepository;
import com.nabgha.book.history.domain.repository.BookTransactionHistoryRepository;
import com.nabgha.book.history.domain.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HistoryUseCaseConfig {

    @Bean
    public BorrowBookUseCase borrowBookUseCase(BookTransactionHistoryRepository historyRepository, BookRepository bookRepository) {
        return new BorrowBookUseCase(historyRepository, bookRepository);
    }

    @Bean
    public ReturnBorrowedBookUseCase returnBorrowedBookUseCase(BookTransactionHistoryRepository historyRepository, BookRepository bookRepository) {
        return new ReturnBorrowedBookUseCase(historyRepository, bookRepository);
    }

    @Bean
    public ApproveReturnBookUseCase approveReturnBookUseCase(BookTransactionHistoryRepository historyRepository, BookRepository bookRepository) {
        return new ApproveReturnBookUseCase(historyRepository, bookRepository);
    }

    @Bean
    public FindAllBorrowedBooksUseCase findAllBorrowedBooksUseCase(BookTransactionHistoryRepository historyRepository) {
        return new FindAllBorrowedBooksUseCase(historyRepository);
    }

    @Bean
    public FindAllReturnedBooksUseCase findAllReturnedBooksUseCase(BookTransactionHistoryRepository historyRepository) {
        return new FindAllReturnedBooksUseCase(historyRepository);
    }

}

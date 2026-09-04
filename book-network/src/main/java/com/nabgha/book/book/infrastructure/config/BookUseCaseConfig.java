package com.nabgha.book.book.infrastructure.config;

import com.nabgha.book.book.domain.repository.BookRepository;
import com.nabgha.book.book.domain.service.BookRatingService;
import com.nabgha.book.book.domain.usecase.*;
import com.nabgha.book.book.infrastructure.file.FileStoragePort;
import com.nabgha.book.feedback.domain.repository.FeedbackRepository;
import com.nabgha.book.history.domain.repository.BookTransactionHistoryRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookUseCaseConfig {

    @Bean
    public CreateBookUseCase createBookUseCase(BookRepository bookRepository) {
        return new CreateBookUseCase(bookRepository);
    }

    @Bean
    public FindBookByIdUseCase findBookByIdUseCase(BookRepository bookRepository) {
        return new FindBookByIdUseCase(bookRepository);
    }

    @Bean
    public FindAllDisplayableBooksUseCase findAllDisplayableBooksUseCase(BookRepository bookRepository) {
        return new FindAllDisplayableBooksUseCase(bookRepository);
    }

    @Bean
    public FindAllBooksByOwnerUseCase findAllBooksByOwnerUseCase(BookRepository bookRepository) {
        return new FindAllBooksByOwnerUseCase(bookRepository);
    }

    @Bean
    public UpdateShareableStatusUseCase updateShareableStatusUseCase(BookRepository bookRepository) {
        return new UpdateShareableStatusUseCase(bookRepository);
    }

    @Bean
    public UploadBookCoverUseCase uploadBookCoverUseCase(BookRepository bookRepository, FileStoragePort fileStoragePort) {
        return new UploadBookCoverUseCase(bookRepository, fileStoragePort);
    }

    @Bean
    public BookRatingService bookRatingService(FeedbackRepository feedbackRepository) {
        return new BookRatingService(feedbackRepository);
    }

    @Bean
    public UpdateArchivedStatusUseCase updateArchivedStatusUseCase(BookRepository bookRepository) {
        return new UpdateArchivedStatusUseCase(bookRepository);
    }

    @Bean
    public DeleteBookUseCase deleteBookUseCase(BookRepository bookRepository, BookTransactionHistoryRepository history) {
        return new DeleteBookUseCase(bookRepository, history);
    }
}

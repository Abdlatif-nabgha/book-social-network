package com.nabgha.book.feedback.infrastructure.config;

import com.nabgha.book.book.domain.repository.BookRepository;
import com.nabgha.book.feedback.domain.repository.FeedbackRepository;
import com.nabgha.book.feedback.domain.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeedbackUseCaseConfig {

    @Bean
    public CreateFeedbackUseCase createFeedbackUseCase(FeedbackRepository feedbackRepository, BookRepository bookRepository) {
        return new CreateFeedbackUseCase(feedbackRepository, bookRepository);
    }

    @Bean
    public FindAllFeedbacksByBookUseCase findAllFeedbacksByBookUseCase(FeedbackRepository feedbackRepository) {
        return new FindAllFeedbacksByBookUseCase(feedbackRepository);
    }

    @Bean
    public UpdateFeedbackUseCase updateFeedbackUseCase(FeedbackRepository feedbackRepository) {
        return new UpdateFeedbackUseCase(feedbackRepository);
    }

    @Bean
    public DeleteFeedbackUseCase deleteFeedbackUseCase(FeedbackRepository feedbackRepository) {
        return new DeleteFeedbackUseCase(feedbackRepository);
    }
}
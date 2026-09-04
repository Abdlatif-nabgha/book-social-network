package com.nabgha.book.feedback.domain.usecase;


import com.nabgha.book.common.domain.PageResult;
import com.nabgha.book.feedback.domain.model.Feedback;
import com.nabgha.book.feedback.domain.repository.FeedbackRepository;

import java.util.List;

public class FindAllFeedbacksByBookUseCase {

    private final FeedbackRepository feedbackRepository;

    public FindAllFeedbacksByBookUseCase(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public PageResult<Feedback> execute(Integer bookId, int page, int size) {
        List<Feedback> feedbacks = feedbackRepository.findAllByBook(page, size, bookId);
        long total = feedbackRepository.countByBook(bookId);
        return new PageResult<>(feedbacks, page, size, total);
    }
}

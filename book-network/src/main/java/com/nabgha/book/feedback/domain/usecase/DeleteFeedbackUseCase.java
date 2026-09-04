package com.nabgha.book.feedback.domain.usecase;


import com.nabgha.book.feedback.domain.exception.FeedbackNotFoundException;
import com.nabgha.book.feedback.domain.exception.FeedbackOperationNotPermittedException;
import com.nabgha.book.feedback.domain.model.Feedback;
import com.nabgha.book.feedback.domain.repository.FeedbackRepository;

public class DeleteFeedbackUseCase {

    private final FeedbackRepository feedbackRepository;

    public DeleteFeedbackUseCase(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public void execute(Integer feedbackId, Integer connectedUser) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new FeedbackNotFoundException(feedbackId));
        if (!feedback.isOwnedBy(connectedUser)) {
            throw new FeedbackOperationNotPermittedException("You cannot delete feedback that isn't' yours");
        }
        feedbackRepository.delete(feedback);
    }
}

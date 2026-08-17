package com.nabgha.book.feedback.domain.usecase;


import com.nabgha.book.feedback.domain.exception.FeedbackNotFoundException;
import com.nabgha.book.feedback.domain.exception.FeedbackOperationNotPermittedException;
import com.nabgha.book.feedback.domain.model.Feedback;
import com.nabgha.book.feedback.domain.repository.FeedbackRepository;

public class UpdateFeedbackUseCase {

    private final FeedbackRepository feedbackRepository;

    public UpdateFeedbackUseCase(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public Feedback execute(Integer feedbackId, Double note, String comment, Integer connectedUserId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new FeedbackNotFoundException(feedbackId));

        if (!feedback.isOwnedBy(connectedUserId)) {
            throw new FeedbackOperationNotPermittedException(
                    "You cannot update feedback that isn't yours"
            );
        }
        if (feedback.getNote() != null) feedback.updateNote(note);
        if (feedback.getComment() != null) feedback.updateComment(comment);

        return feedbackRepository.save(feedback);
    }
}

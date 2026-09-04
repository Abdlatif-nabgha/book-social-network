package com.nabgha.book.feedback.domain.usecase;


import com.nabgha.book.book.domain.exception.BookNotFoundException;
import com.nabgha.book.book.domain.exception.BookOperationNotPermittedException;
import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.domain.repository.BookRepository;
import com.nabgha.book.feedback.domain.model.Feedback;
import com.nabgha.book.feedback.domain.repository.FeedbackRepository;

public class CreateFeedbackUseCase {

    private final FeedbackRepository feedbackRepository;
    private final BookRepository bookRepository;

    public CreateFeedbackUseCase(FeedbackRepository feedbackRepository, BookRepository bookRepository) {
        this.feedbackRepository = feedbackRepository;
        this.bookRepository = bookRepository;
    }

    public Feedback execute(Double note, String comment, Integer bookId, Integer connectedUserId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        if (book.isAvailableForBorrowing()) {
            throw new BookOperationNotPermittedException(
                    "You cannot give feedback for an archived or non-shareable book"
            );
        }

        if (book.isOwnedBy(connectedUserId)) {
            throw new BookOperationNotPermittedException(
                    "You cannot give feedback to your own book"
            );
        }
        Feedback feedback = Feedback.create(note, comment, bookId, connectedUserId);
        return feedbackRepository.save(feedback);
    }
}

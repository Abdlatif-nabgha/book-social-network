package com.nabgha.book.feedback;

import com.nabgha.book.book.Book;
import com.nabgha.book.book.BookRepository;
import com.nabgha.book.exception.OperationNotPermittedException;
import com.nabgha.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;

    @Transactional
    public FeedbackResponse save(FeedbackRequest request, User user) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book with id: " + request.bookId()));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You cannot give feedback for an archived or non-shareable book");
        }
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot give feedback to  yourself");
        }
        Feedback feedback = feedbackMapper.toFeedback(request);
        feedback.setBook(book);
        Feedback feedbackSaved = feedbackRepository.save(feedback);
        return feedbackMapper.toFeedbackResponse(feedbackSaved);
    }
}

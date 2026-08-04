package com.nabgha.book.feedback;

import com.nabgha.book.book.Book;
import com.nabgha.book.book.BookRepository;
import com.nabgha.book.common.PageResponse;
import com.nabgha.book.exception.OperationNotPermittedException;
import com.nabgha.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        return feedbackMapper.toFeedbackResponse(feedbackSaved, user.getId());
    }

    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId, int page, int size, User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        Page<Feedback> feedbacks = feedbackRepository.findAllFeedbacksByBook(bookId, pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(feedback ->  feedbackMapper.toFeedbackResponse(feedback, user.getId()))
                .toList();
        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }

    @Transactional
    public FeedbackResponse updateFeedback(Integer feedbackId, FeedbackUpdateRequest request, User user) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new EntityNotFoundException("No feedback found with id: " + feedbackId));

        if (!Objects.equals(feedback.getCreatedBy(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update feedback that isn't yours");
        }

        if (request.note() != null) {
            feedback.setNote(request.note());
        }
        if (request.comment() != null) {
            feedback.setComment(request.comment());
        }

        Feedback updated = feedbackRepository.save(feedback);
        return feedbackMapper.toFeedbackResponse(updated, user.getId());
    }

    @Transactional
    public void deleteFeedback(Integer feedbackId, User user) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new EntityNotFoundException("No feedback found with id: " + feedbackId));

        if (!Objects.equals(feedback.getCreatedBy(), user.getId())) {
            throw new OperationNotPermittedException("You cannot delete feedback that isn't yours");
        }

        feedbackRepository.delete(feedback);
    }
}

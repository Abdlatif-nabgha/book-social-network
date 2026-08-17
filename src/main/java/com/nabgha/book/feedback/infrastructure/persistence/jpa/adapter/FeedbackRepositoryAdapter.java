package com.nabgha.book.feedback.infrastructure.persistence.jpa.adapter;

import com.nabgha.book.book.infrastructure.persistence.jpa.entity.BookEntity;
import com.nabgha.book.book.infrastructure.persistence.jpa.repository.BookJpaRepository;
import com.nabgha.book.feedback.domain.model.Feedback;
import com.nabgha.book.feedback.domain.repository.FeedbackRepository;
import com.nabgha.book.feedback.infrastructure.persistence.jpa.entity.FeedbackEntity;
import com.nabgha.book.feedback.infrastructure.persistence.jpa.mapper.FeedbackJpaMapper;
import com.nabgha.book.feedback.infrastructure.persistence.jpa.repository.FeedbackJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FeedbackRepositoryAdapter implements FeedbackRepository {

    private final FeedbackJpaRepository feedbackJpaRepository;
    private final BookJpaRepository bookJpaRepository;
    private final FeedbackJpaMapper feedbackJpaMapper;

    public FeedbackRepositoryAdapter(FeedbackJpaRepository feedbackJpaRepository,
                                     BookJpaRepository bookJpaRepository,
                                     FeedbackJpaMapper feedbackJpaMapper) {
        this.feedbackJpaRepository = feedbackJpaRepository;
        this.bookJpaRepository = bookJpaRepository;
        this.feedbackJpaMapper = feedbackJpaMapper;
    }

    @Override
    public Feedback save(Feedback feedback) {
        BookEntity book = bookJpaRepository.findById(feedback.getBookId())
                .orElseThrow(() -> new IllegalStateException("Book not found with id: " + feedback.getBookId()));
        FeedbackEntity entity = feedbackJpaMapper.toEntity(feedback, book);
        FeedbackEntity saved = feedbackJpaRepository.save(entity);
        return feedbackJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Feedback> findById(Integer id) {
        return feedbackJpaRepository.findById(id).map(feedbackJpaMapper::toDomain);
    }

    @Override
    public List<Feedback> findAllByBook(int page, int size, Integer bookId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        return feedbackJpaRepository.findAllByBook(pageable, bookId)
                .stream().map(feedbackJpaMapper::toDomain).toList();
    }

    @Override
    public long countByBook(int bookId) {
        return feedbackJpaRepository.countByBook(bookId);
    }

    @Override
    public List<Feedback> findAllByBookId(Integer bookId) {
        return feedbackJpaRepository.findAllByBookId(bookId)
                .stream().map(feedbackJpaMapper::toDomain).toList();
    }

    @Override
    public void delete(Feedback feedback) {
        feedbackJpaRepository.deleteById(feedback.getId());
    }
}
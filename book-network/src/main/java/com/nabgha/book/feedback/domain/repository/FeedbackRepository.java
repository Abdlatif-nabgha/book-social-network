package com.nabgha.book.feedback.domain.repository;


import com.nabgha.book.feedback.domain.model.Feedback;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository {

    Feedback save(Feedback feedback);

    Optional<Feedback> findById(Integer id);

    List<Feedback> findAllByBook(int page, int size, Integer bookId);

    long countByBook(int bookId);

    List<Feedback> findAllByBookId(Integer bookId);

    void delete(Feedback feedback);

}

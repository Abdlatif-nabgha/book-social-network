package com.nabgha.book.feedback.infrastructure.persistence.jpa.repository;

import com.nabgha.book.feedback.infrastructure.persistence.jpa.entity.FeedbackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedbackJpaRepository extends JpaRepository<FeedbackEntity, Integer> {

    @Query("""
        SELECT feedback
        FROM FeedbackEntity feedback
        WHERE feedback.book.id = :bookId
        """)
    Page<FeedbackEntity> findAllByBook(Pageable pageable, Integer bookId);

    @Query("""
        SELECT COUNT(feedback)
        FROM FeedbackEntity feedback
        WHERE feedback.book.id = :bookId
        """)
    long countByBook(Integer bookId);

    List<FeedbackEntity> findAllByBookId(Integer bookId);
}
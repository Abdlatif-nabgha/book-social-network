package com.nabgha.book.book.infrastructure.persistence.jpa.repository;

import com.nabgha.book.book.infrastructure.persistence.jpa.entity.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface BookJpaRepository extends JpaRepository<BookEntity, Integer>, JpaSpecificationExecutor<BookEntity> {

    @Query("""
        SELECT book
        FROM BookEntity book
        WHERE book.archived = false
        AND book.shareable = true
        AND book.owner.id != :userId
        """)
    Page<BookEntity> findAllDisplayableBooks(Pageable pageable, Integer userId);

    @Query("""
        SELECT COUNT(book)
        FROM BookEntity book
        WHERE book.archived = false
        AND book.shareable = true
        AND book.owner.id != :userId
        """)
    long countDisplayableBooks(Integer userId);

}
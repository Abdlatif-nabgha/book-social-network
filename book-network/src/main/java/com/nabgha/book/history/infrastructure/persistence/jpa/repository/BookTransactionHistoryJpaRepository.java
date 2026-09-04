package com.nabgha.book.history.infrastructure.persistence.jpa.repository;

import com.nabgha.book.history.infrastructure.persistence.jpa.entity.BookTransactionHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookTransactionHistoryJpaRepository extends JpaRepository<BookTransactionHistoryEntity, Integer> {

    @Query("""
        SELECT history
        FROM BookTransactionHistoryEntity history
        WHERE history.user.id = :userId
        """)
    Page<BookTransactionHistoryEntity> findAllBorrowedByUser(Pageable pageable, Integer userId);

    @Query("""
        SELECT COUNT(history)
        FROM BookTransactionHistoryEntity history
        WHERE history.user.id = :userId
        """)
    long countBorrowedByUser(Integer userId);

    @Query("""
        SELECT history
        FROM BookTransactionHistoryEntity history
        WHERE history.book.owner.id = :ownerId
        AND history.returned = true
        """)
    Page<BookTransactionHistoryEntity> findAllReturnedByOwner(Pageable pageable, Integer ownerId);

    @Query("""
        SELECT COUNT(history)
        FROM BookTransactionHistoryEntity history
        WHERE history.book.owner.id = :ownerId
        AND history.returned = true
        """)
    long countReturnedByOwner(Integer ownerId);

    @Query("""
        SELECT (COUNT(history) > 0)
        FROM BookTransactionHistoryEntity history
        WHERE history.book.id = :bookId
        AND history.returnedApproved = false
        """)
    boolean isBookCurrentlyBorrowed(Integer bookId);

    @Query("""
        SELECT history
        FROM BookTransactionHistoryEntity history
        WHERE history.book.id = :bookId
        AND history.user.id = :userId
        AND history.returnedApproved = false
        """)
    Optional<BookTransactionHistoryEntity> findActiveBorrowByBookAndUser(Integer bookId, Integer userId);

    @Query("""
        SELECT history
        FROM BookTransactionHistoryEntity history
        WHERE history.book.id = :bookId
        AND history.book.owner.id = :ownerId
        AND history.returned = true
        AND history.returnedApproved = false
        """)
    Optional<BookTransactionHistoryEntity> findReturnedNotApprovedByBookAndOwner(Integer bookId, Integer ownerId);
}
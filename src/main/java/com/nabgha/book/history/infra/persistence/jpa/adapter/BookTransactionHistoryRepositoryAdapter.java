package com.nabgha.book.history.infrastructure.persistence.jpa.adapter;


import com.nabgha.book.book.infrastructure.persistence.jpa.entity.BookEntity;
import com.nabgha.book.book.infrastructure.persistence.jpa.repository.BookJpaRepository;
import com.nabgha.book.history.domain.model.BookTransactionHistory;
import com.nabgha.book.history.domain.repository.BookTransactionHistoryRepository;
import com.nabgha.book.history.infrastructure.persistence.jpa.entity.BookTransactionHistoryEntity;
import com.nabgha.book.history.infrastructure.persistence.jpa.mapper.BookTransactionHistoryJpaMapper;
import com.nabgha.book.history.infrastructure.persistence.jpa.repo.BookTransactionHistoryJpaRepository;
import com.nabgha.book.user.infrastructure.persistence.jpa.entity.UserEntity;
import com.nabgha.book.user.infrastructure.persistence.jpa.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookTransactionHistoryRepositoryAdapter implements BookTransactionHistoryRepository {

    private final BookTransactionHistoryJpaRepository historyJpaRepository;
    private final BookJpaRepository bookJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final BookTransactionHistoryJpaMapper mapper;

    @Override
    public BookTransactionHistory save(BookTransactionHistory history) {
        BookEntity book = bookJpaRepository.findById(history.getBookId())
                .orElseThrow(() -> new IllegalStateException("Book not found with id: " + history.getBookId()));
        UserEntity user = userJpaRepository.findById(history.getUserId())
                .orElseThrow(() -> new IllegalStateException("User not found with id: " + history.getUserId()));
        BookTransactionHistoryEntity entity = mapper.toEntity(history, book, user);
        BookTransactionHistoryEntity saved = historyJpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<BookTransactionHistory> findAllBorrowedByUser(int page, int size, Integer userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        return historyJpaRepository.findAllBorrowedByUser(pageable, userId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countBorrowedByUser(Integer userId) {
        return historyJpaRepository.countBorrowedByUser(userId);
    }

    @Override
    public List<BookTransactionHistory> findAllReturnedByOwner(int page, int size, Integer ownerId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        return historyJpaRepository.findAllReturnedByOwner(pageable, ownerId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countReturnedByOwner(Integer ownerId) {
        return historyJpaRepository.countReturnedByOwner(ownerId);
    }

    @Override
    public boolean isBookCurrentlyBorrowed(Integer bookId) {
        return historyJpaRepository.isBookCurrentlyBorrowed(bookId);
    }

    @Override
    public Optional<BookTransactionHistory> findActiveBorrowByBookAndUser(Integer bookId, Integer userId) {
        return historyJpaRepository.findActiveBorrowByBookAndUser(bookId, userId).map(mapper::toDomain);
    }

    @Override
    public Optional<BookTransactionHistory> findReturnedNotApprovedByBookAndOwner(Integer bookId, Integer ownerId) {
        return historyJpaRepository.findReturnedNotApprovedByBookAndOwner(bookId, ownerId).map(mapper::toDomain);
    }

}

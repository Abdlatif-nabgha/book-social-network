package com.nabgha.book.book.infrastructure.persistence.jpa.adapter;

import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.domain.repository.BookRepository;
import com.nabgha.book.book.infrastructure.persistence.jpa.entity.BookEntity;
import com.nabgha.book.book.infrastructure.persistence.jpa.mapper.BookJpaMapper;
import com.nabgha.book.book.infrastructure.persistence.jpa.repository.BookJpaRepository;
import com.nabgha.book.user.infrastructure.persistence.jpa.entity.UserEntity;
import com.nabgha.book.user.infrastructure.persistence.jpa.repository.UserJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Component
public class BookRepositoryAdapter implements BookRepository {

    private final BookJpaRepository bookJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final BookJpaMapper bookJpaMapper;

    public BookRepositoryAdapter(BookJpaRepository bookJpaRepository,
                                 UserJpaRepository userJpaRepository,
                                 BookJpaMapper bookJpaMapper) {
        this.bookJpaRepository = bookJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.bookJpaMapper = bookJpaMapper;
    }

    @Override
    public Book save(Book book) {
        UserEntity owner = userJpaRepository.findById(book.getOwnerId())
                .orElseThrow(() -> new IllegalStateException("Owner not found with id: " + book.getOwnerId()));
        BookEntity entity = bookJpaMapper.toEntity(book, owner);
        BookEntity saved = bookJpaRepository.save(entity);
        return bookJpaMapper.toDomain(saved);
    }

    public Optional<Book> findById(Integer id) {
        return bookJpaRepository.findById(id).map(bookJpaMapper::toDomain);
    }

    @Override
    public List<Book> findAllDisplayableBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        return bookJpaRepository.findAllDisplayableBooks(pageable)
                .stream().map(bookJpaMapper::toDomain).toList();
    }

    @Override
    public long countDisplayableBooks() {
        return bookJpaRepository.countDisplayableBooks();
    }

    @Override
    public List<Book> findAllByOwner(int page, int size, Integer ownerId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        return bookJpaRepository.findAll(withOwnerId(ownerId), pageable)
                .stream().map(bookJpaMapper::toDomain).toList();
    }

    @Override
    public long countByOwner(Integer ownerId) {
        return bookJpaRepository.count(withOwnerId(ownerId));
    }

    @Override
    public boolean existsById(Integer id) {
        return bookJpaRepository.existsById(id);
    }

    @Override
    public long countAll() {
        return userJpaRepository.count();
    }

    private org.springframework.data.jpa.domain.Specification<BookEntity> withOwnerId(Integer ownerId) {
        return (root, query, cb) -> cb.equal(root.get("owner").get("id"), ownerId);
    }

    @Override
    public void deleteById(Integer id) {
        bookJpaRepository.deleteById(id);
    }
}
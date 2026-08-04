package com.nabgha.book.book.domain.repository;

import com.nabgha.book.book.domain.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    Book save(Book book);

    Optional<Book> findById(Integer id);

    List<Book> findAllDisplayableBooks(int page, int size, Integer excludedOwnerId);

    long countDisplayableBooks(Integer excludedOwnerId);

    List<Book> findAllByOwner(int page, int size, Integer ownerId);

    long countByOwner(Integer ownerId);

    boolean existsById(Integer id);
}
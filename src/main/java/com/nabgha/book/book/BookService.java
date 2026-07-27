package com.nabgha.book.book;

import com.nabgha.book.common.PageResponse;
import com.nabgha.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;

    @Transactional
    public BookResponse save(BookRequest request, User connectedUser) {
        // Authentication connectedUser
        //User user = ((User) connectedUser.getPrincipal());
        Book book = bookMapper.toBook(request);
        book.setOwner(connectedUser);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toBookDto(savedBook);
    }

    public BookResponse findById(Integer bookId) {
        return  bookRepository.findById(bookId)
                .map(bookMapper::toBookDto)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
    }


    public PageResponse<BookResponse> findAllBooks(int page, int size, User user) {
        // 1.
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        // 2.
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        // 3.
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookDto)
                .toList();
        // 4.
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }
}

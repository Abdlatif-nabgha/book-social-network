package com.nabgha.book.book;

import com.nabgha.book.common.PageResponse;
import com.nabgha.book.exception.OperationNotPermittedException;
import com.nabgha.book.history.BookTransactionHistory;
import com.nabgha.book.history.BookTransactionHistoryRepository;
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
import java.util.Objects;

import static com.nabgha.book.book.BookSpecification.withOwnerId;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;

    @Transactional
    public BookResponse save(BookRequest request, User connectedUser) {
        // Authentication connectedUser -> another choice
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
        // 1. Create a Pageable object with pagination parameters (page, size) sorted by creation date in descending order
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        
        // 2. Fetch all displayable books from the database filtered by the current user's ID
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        
        // 3. Convert each Book entity to BookResponse DTO using the mapper and collect into a list
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookDto)
                .toList();
        
        // 4. Build and return a PageResponse wrapper containing the book responses and pagination metadata
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

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        Page<Book> books = bookRepository.findAll(withOwnerId(user.getId()), pageable);

        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookDto)
                .toList();

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

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());

        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());

        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();

        return new PageResponse<>(
                bookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());

        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());

        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();

        return new PageResponse<>(
                bookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    @Transactional
    public BookResponse updateShareableStatus(Integer bookId, User user) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id: "+bookId));
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update others books shareable status ");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookMapper.toBookDto(book);
    }

    @Transactional
    public BookResponse updateArchivedStatus(Integer bookId, User user) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id: "+bookId));
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update others books archived status ");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookMapper.toBookDto(book);
    }

    @Transactional
    public BorrowedBookResponse borrowBook(Integer bookId, User user) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id: "+bookId));

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("This book is not shareable");
        }
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow your own books ");
        }

        final boolean isAlreadyBorrowed = bookTransactionHistoryRepository.isAlreadyBorrowedByOtherUser(bookId);
        if (isAlreadyBorrowed) {
            throw new OperationNotPermittedException("The requested book is already borrowed");
        }
        BookTransactionHistory history = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnedApproved(false)
                .build();
        BookTransactionHistory savedHistory = bookTransactionHistoryRepository.save(history);
        return bookMapper.toBorrowedBookResponse(savedHistory);
    }

    @Transactional
    public BorrowedBookResponse returnBorrowedBook(Integer bookId, User user) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id: " + bookId));

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("This book is not shareable");
        }
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot return your own book");
        }

        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository
                .findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));

        bookTransactionHistory.setReturned(true);
        // returnedApproved stays false — owner hasn't confirmed yet
        BookTransactionHistory history = bookTransactionHistoryRepository.save(bookTransactionHistory);
        return bookMapper.toBorrowedBookResponse(history);
    }

    @Transactional
    public BorrowedBookResponse approveReturnBorrowedBook(Integer bookId, User user) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id: " + bookId));

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("This book is not shareable");
        }
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot approve the return of a book you don't own");
        }

        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository
                .findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet, you cannot approve its return"));

        bookTransactionHistory.setReturnedApproved(true);
        BookTransactionHistory history = bookTransactionHistoryRepository.save(bookTransactionHistory);
        return bookMapper.toBorrowedBookResponse(history);
    }
}

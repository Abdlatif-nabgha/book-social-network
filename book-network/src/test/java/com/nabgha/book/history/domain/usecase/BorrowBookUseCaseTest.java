package com.nabgha.book.history.domain.usecase;

import com.nabgha.book.book.domain.exception.BookNotFoundException;
import com.nabgha.book.book.domain.exception.BookOperationNotPermittedException;
import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.domain.repository.BookRepository;
import com.nabgha.book.history.domain.model.BookTransactionHistory;
import com.nabgha.book.history.domain.repository.BookTransactionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowBookUseCaseTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookTransactionHistoryRepository historyRepository;

    private BorrowBookUseCase borrowBookUseCase;

    @BeforeEach
    void setUp() {
        borrowBookUseCase = new BorrowBookUseCase(historyRepository, bookRepository);
    }

    @Test
    @DisplayName("Should borrow book successfully when available and not owned by borrower")
    void shouldBorrowBookSuccessfully() {
        Integer bookId = 1;
        Integer borrowerId = 20;
        Integer ownerId = 10;

        Book book = Book.reconstitute(
                bookId, "Clean Architecture", "Robert Martin", "978-0134494166", "Software Architecture",
                null, false, true, ownerId, "Abd Latif", LocalDateTime.now()
        );

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(historyRepository.isBookCurrentlyBorrowed(bookId)).thenReturn(false);
        when(historyRepository.save(any(BookTransactionHistory.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        BookTransactionHistory history = borrowBookUseCase.execute(bookId, borrowerId);

        assertThat(history).isNotNull();
        assertThat(history.getBookId()).isEqualTo(bookId);
        assertThat(history.getUserId()).isEqualTo(borrowerId);
        assertThat(history.isReturned()).isFalse();
        assertThat(history.isReturnedApproved()).isFalse();

        verify(historyRepository, times(1)).save(any(BookTransactionHistory.class));
    }

    @Test
    @DisplayName("Should throw BookOperationNotPermittedException when user tries to borrow their own book")
    void shouldThrowExceptionWhenBorrowingOwnBook() {
        Integer bookId = 1;
        Integer ownerId = 10;

        Book book = Book.reconstitute(
                bookId, "Clean Architecture", "Robert Martin", "978-0134494166", "Software Architecture",
                null, false, true, ownerId, "Abd Latif", LocalDateTime.now()
        );

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> borrowBookUseCase.execute(bookId, ownerId))
                .isInstanceOf(BookOperationNotPermittedException.class)
                .hasMessageContaining("You cannot borrow your own book");

        verify(historyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BookOperationNotPermittedException when book is already borrowed")
    void shouldThrowExceptionWhenBookAlreadyBorrowed() {
        Integer bookId = 1;
        Integer borrowerId = 20;
        Integer ownerId = 10;

        Book book = Book.reconstitute(
                bookId, "Clean Architecture", "Robert Martin", "978-0134494166", "Software Architecture",
                null, false, true, ownerId, "Abd Latif", LocalDateTime.now()
        );

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(historyRepository.isBookCurrentlyBorrowed(bookId)).thenReturn(true);

        assertThatThrownBy(() -> borrowBookUseCase.execute(bookId, borrowerId))
                .isInstanceOf(BookOperationNotPermittedException.class)
                .hasMessageContaining("This book is already borrowed");

        verify(historyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BookNotFoundException when book does not exist")
    void shouldThrowExceptionWhenBookNotFound() {
        Integer bookId = 99;
        Integer borrowerId = 20;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> borrowBookUseCase.execute(bookId, borrowerId))
                .isInstanceOf(BookNotFoundException.class);

        verify(historyRepository, never()).save(any());
    }
}

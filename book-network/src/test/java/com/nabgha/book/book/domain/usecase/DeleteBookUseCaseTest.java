package com.nabgha.book.book.domain.usecase;

import com.nabgha.book.book.domain.exception.BookNotFoundException;
import com.nabgha.book.book.domain.exception.BookOperationNotPermittedException;
import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.domain.repository.BookRepository;
import com.nabgha.book.history.domain.repository.BookTransactionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteBookUseCaseTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookTransactionHistoryRepository historyRepository;

    private DeleteBookUseCase deleteBookUseCase;

    @BeforeEach
    void setUp() {
        deleteBookUseCase = new DeleteBookUseCase(bookRepository, historyRepository);
    }

    @Test
    @DisplayName("Should successfully delete book when owned by user and not borrowed")
    void shouldSuccessfullyDeleteBookWhenOwnedAndNotBorrowed() {
        Integer bookId = 1;
        Integer ownerId = 10;
        Book book = Book.reconstitute(
                bookId, "Refactoring", "Martin Fowler", "978-0201485677", "Improving Code", null,
                false, true, ownerId, "Abd Latif", LocalDateTime.now()
        );

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(historyRepository.isBookCurrentlyBorrowed(bookId)).thenReturn(false);

        deleteBookUseCase.execute(bookId, ownerId);

        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    @DisplayName("Should throw BookNotFoundException when book does not exist")
    void shouldThrowExceptionWhenBookNotFound() {
        Integer bookId = 99;
        Integer userId = 10;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteBookUseCase.execute(bookId, userId))
                .isInstanceOf(BookNotFoundException.class);

        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should throw BookOperationNotPermittedException when user does not own book")
    void shouldThrowExceptionWhenUserDoesNotOwnBook() {
        Integer bookId = 1;
        Integer ownerId = 10;
        Integer unauthorizedUser = 20;

        Book book = Book.reconstitute(
                bookId, "Refactoring", "Martin Fowler", "978-0201485677", "Improving Code", null,
                false, true, ownerId, "Abd Latif", LocalDateTime.now()
        );

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> deleteBookUseCase.execute(bookId, unauthorizedUser))
                .isInstanceOf(BookOperationNotPermittedException.class)
                .hasMessageContaining("You cannot delete a book you don't own");

        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should throw BookOperationNotPermittedException when book is currently borrowed")
    void shouldThrowExceptionWhenBookIsCurrentlyBorrowed() {
        Integer bookId = 1;
        Integer ownerId = 10;

        Book book = Book.reconstitute(
                bookId, "Refactoring", "Martin Fowler", "978-0201485677", "Improving Code", null,
                false, true, ownerId, "Abd Latif", LocalDateTime.now()
        );

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(historyRepository.isBookCurrentlyBorrowed(bookId)).thenReturn(true);

        assertThatThrownBy(() -> deleteBookUseCase.execute(bookId, ownerId))
                .isInstanceOf(BookOperationNotPermittedException.class)
                .hasMessageContaining("You cannot delete a book that is currently borrowed");

        verify(bookRepository, never()).deleteById(any());
    }
}

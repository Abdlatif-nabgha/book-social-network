package com.nabgha.book.book.domain.usecase;

import com.nabgha.book.book.domain.exception.BookNotFoundException;
import com.nabgha.book.book.domain.exception.BookOperationNotPermittedException;
import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.domain.repository.BookRepository;
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
class UpdateArchivedStatusUseCaseTest {

    @Mock
    private BookRepository bookRepository;

    private UpdateArchivedStatusUseCase updateArchivedStatusUseCase;

    @BeforeEach
    void setUp() {
        updateArchivedStatusUseCase = new UpdateArchivedStatusUseCase(bookRepository);
    }

    @Test
    @DisplayName("Should successfully toggle archived status when owned by user")
    void shouldSuccessfullyToggleArchivedStatusWhenOwnedByUser() {
        Integer bookId = 1;
        Integer ownerId = 10;
        Book book = Book.reconstitute(
                bookId, "Clean Code", "Robert Martin", "1234567890", "Synopsis", null,
                false, true, ownerId, "Abd Latif", LocalDateTime.now()
        );

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        Book result = updateArchivedStatusUseCase.execute(bookId, ownerId);

        assertThat(result).isNotNull();
        assertThat(result.isArchived()).isTrue();
        assertThat(result.isShareable()).isFalse();
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("Should throw BookNotFoundException when book does not exist")
    void shouldThrowExceptionWhenBookNotFound() {
        Integer bookId = 99;
        Integer userId = 10;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateArchivedStatusUseCase.execute(bookId, userId))
                .isInstanceOf(BookNotFoundException.class);

        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BookOperationNotPermittedException when user does not own the book")
    void shouldThrowExceptionWhenUserDoesNotOwnBook() {
        Integer bookId = 1;
        Integer ownerId = 10;
        Integer differentUserId = 20;

        Book book = Book.reconstitute(
                bookId, "Clean Code", "Robert Martin", "1234567890", "Synopsis", null,
                false, true, ownerId, "Abd Latif", LocalDateTime.now()
        );

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> updateArchivedStatusUseCase.execute(bookId, differentUserId))
                .isInstanceOf(BookOperationNotPermittedException.class)
                .hasMessageContaining("You cannot update the archive status of a book you don't own");

        verify(bookRepository, never()).save(any());
    }
}

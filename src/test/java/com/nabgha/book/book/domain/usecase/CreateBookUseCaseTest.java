package com.nabgha.book.book.domain.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.domain.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
public class CreateBookUseCaseTest {
    
    @Mock 
    private BookRepository bookRepository;

    private CreateBookUseCase createBookUseCase;

    @BeforeEach 
    void setUp() {
        createBookUseCase = new CreateBookUseCase(bookRepository);
    }

    @Test 
    @DisplayName("Should successfully create and save a new book with default values")
    void shouldCreateBookSuccessfully() {
        String title = "Clean Code";
        String author = "Robert C. Martin";
        String isbn = "978-0132350884";
        String synopsis = "A handbook of agile software craftsmanship.";
        Integer ownerId = 1;
        String ownerFullName = "Abd Latif Nabgha";

        when(bookRepository.save(any(Book.class)))
        .thenAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            return book;
        });

        Book createdBook = createBookUseCase.execute(title, author, isbn, synopsis, ownerId, ownerFullName);

                // Assert (Then) using AssertJ
        assertThat(createdBook).isNotNull();
        assertThat(createdBook.getTitle()).isEqualTo(title);
        assertThat(createdBook.getAuthor()).isEqualTo(author);
        assertThat(createdBook.getIsbn()).isEqualTo(isbn);
        assertThat(createdBook.isShareable()).isTrue();
        assertThat(createdBook.isArchived()).isFalse();

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        
        verify(bookRepository, times(1)).save(bookCaptor.capture());

        Book capturedBook = bookCaptor.getValue();
        assertThat(capturedBook.getOwnerId()).isEqualTo(ownerId);
    }
}

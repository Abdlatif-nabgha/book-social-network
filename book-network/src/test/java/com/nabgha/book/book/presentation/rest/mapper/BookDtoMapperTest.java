package com.nabgha.book.book.presentation.rest.mapper;

import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.domain.service.BookRatingService;
import com.nabgha.book.book.infrastructure.file.FileStoragePort;
import com.nabgha.book.book.presentation.rest.dto.BookResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookDtoMapperTest {

    @Mock
    private BookRatingService bookRatingService;

    @Mock
    private FileStoragePort fileStoragePort;

    private BookDtoMapper bookDtoMapper;

    @BeforeEach
    void setUp() {
        bookDtoMapper = new BookDtoMapper(bookRatingService, fileStoragePort);
    }

    @Test
    @DisplayName("Should map Book domain entity to BookResponse DTO with computed rating and cover bytes")
    void shouldMapBookToResponse() {
        Integer bookId = 1;
        Book book = Book.reconstitute(
                bookId, "Effective Java", "Joshua Bloch", "978-0134685991", "Best practices",
                "/covers/effective_java.jpg", false, true, 10, "Abd Latif", LocalDateTime.now()
        );

        byte[] coverBytes = new byte[]{1, 2, 3};
        when(fileStoragePort.read("/covers/effective_java.jpg")).thenReturn(coverBytes);
        when(bookRatingService.calculateAverageRating(bookId)).thenReturn(4.8);

        BookResponse response = bookDtoMapper.toResponse(book);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(bookId);
        assertThat(response.title()).isEqualTo("Effective Java");
        assertThat(response.author()).isEqualTo("Joshua Bloch");
        assertThat(response.rate()).isEqualTo(4.8);
        assertThat(response.bookCover()).isEqualTo(coverBytes);
    }
}

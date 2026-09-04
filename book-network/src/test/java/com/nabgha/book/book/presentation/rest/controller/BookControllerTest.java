package com.nabgha.book.book.presentation.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.domain.usecase.*;
import com.nabgha.book.book.presentation.rest.dto.BookResponse;
import com.nabgha.book.book.presentation.rest.mapper.BookDtoMapper;
import com.nabgha.book.common.domain.PageResult;
import com.nabgha.book.common.presentation.PageResponse;
import com.nabgha.book.user.infrastructure.persistence.jpa.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock private CreateBookUseCase createBookUseCase;
    @Mock private FindBookByIdUseCase findBookByIdUseCase;
    @Mock private FindAllDisplayableBooksUseCase findAllDisplayableBooksUseCase;
    @Mock private FindAllBooksByOwnerUseCase findAllBooksByOwnerUseCase;
    @Mock private UpdateShareableStatusUseCase updateShareableStatusUseCase;
    @Mock private UploadBookCoverUseCase uploadBookCoverUseCase;
    @Mock private BookDtoMapper bookDtoMapper;
    @Mock private UpdateArchivedStatusUseCase updateArchivedStatusUseCase;
    @Mock private DeleteBookUseCase deleteBookUseCase;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        BookController controller = new BookController(
                createBookUseCase,
                findBookByIdUseCase,
                findAllDisplayableBooksUseCase,
                findAllBooksByOwnerUseCase,
                updateShareableStatusUseCase,
                uploadBookCoverUseCase,
                bookDtoMapper,
                updateArchivedStatusUseCase,
                deleteBookUseCase
        );

        HandlerMethodArgumentResolver authenticationPrincipalResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                UserEntity mockUser = new UserEntity();
                mockUser.setId(10);
                mockUser.setFirstName("Abd Latif");
                mockUser.setLastName("Nabgha");
                return mockUser;
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(authenticationPrincipalResolver)
                .build();
    }

    @Test
    @DisplayName("GET /books/{id} - Should return 200 OK and book data")
    void shouldFindBookById() throws Exception {
        Integer bookId = 1;
        Book book = Book.reconstitute(
                bookId, "Clean Code", "Robert Martin", "1234567890", "Synopsis", null,
                false, true, 10, "Abd Latif", LocalDateTime.now()
        );
        BookResponse response = BookResponse.builder()
                .id(bookId)
                .title("Clean Code")
                .author("Robert Martin")
                .isbn("1234567890")
                .synopsis("Synopsis")
                .ownerName("Abd Latif")
                .ownerId(10)
                .archived(false)
                .shareable(true)
                .build();

        when(findBookByIdUseCase.execute(bookId)).thenReturn(book);
        when(bookDtoMapper.toResponse(book)).thenReturn(response);

        mockMvc.perform(get("/books/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(bookId))
                .andExpect(jsonPath("$.data.title").value("Clean Code"))
                .andExpect(jsonPath("$.data.author").value("Robert Martin"));
    }

    @Test
    @DisplayName("GET /books - Should return paginated books list")
    void shouldFindAllDisplayableBooks() throws Exception {
        BookResponse bookResponse = BookResponse.builder()
                .id(1)
                .title("Design Patterns")
                .author("Gang of Four")
                .build();

        PageResponse<BookResponse> pageResponse = PageResponse.<BookResponse>builder()
                .content(List.of(bookResponse))
                .number(0)
                .size(10)
                .totalElements(1L)
                .totalPages(1)
                .first(true)
                .last(true)
                .build();

        PageResult<Book> pageResult = new PageResult<>(List.of(), 0, 10, 1L);
        when(findAllDisplayableBooksUseCase.execute(0, 10)).thenReturn(pageResult);
        when(bookDtoMapper.toPageResponse(pageResult)).thenReturn(pageResponse);

        mockMvc.perform(get("/books?page=0&size=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Design Patterns"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("DELETE /books/{id} - Should return 200 OK")
    void shouldDeleteBookSuccessfully() throws Exception {
        Integer bookId = 1;

        doNothing().when(deleteBookUseCase).execute(eq(bookId), any());

        mockMvc.perform(delete("/books/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book deleted successfully"));
    }
}

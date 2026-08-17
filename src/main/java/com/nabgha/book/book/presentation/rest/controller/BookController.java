package com.nabgha.book.book.presentation.rest.controller;

import com.nabgha.book.book.domain.model.Book;
import com.nabgha.book.book.domain.usecase.*;
import com.nabgha.book.book.presentation.rest.dto.BookRequest;
import com.nabgha.book.book.presentation.rest.dto.BookResponse;
import com.nabgha.book.book.presentation.rest.mapper.BookDtoMapper;
import com.nabgha.book.common.domain.PageResult;
import com.nabgha.book.common.presentation.ApiResponse;
import com.nabgha.book.common.presentation.PageResponse;
import com.nabgha.book.user.infrastructure.persistence.jpa.entity.UserEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/books")
@Tag(name = "Book")
@RequiredArgsConstructor
public class BookController {

    private final CreateBookUseCase createBookUseCase;
    private final FindBookByIdUseCase findBookByIdUseCase;
    private final FindAllDisplayableBooksUseCase findAllDisplayableBooksUseCase;
    private final FindAllBooksByOwnerUseCase findAllBooksByOwnerUseCase;
    private final UpdateShareableStatusUseCase updateShareableStatusUseCase;
    private final UploadBookCoverUseCase uploadBookCoverUseCase;
    private final BookDtoMapper bookDtoMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<BookResponse>> saveBook(
            @Valid @RequestBody BookRequest request,
            @AuthenticationPrincipal UserEntity connectedUser
    ) {
        Book book = createBookUseCase.execute(
                request.title(), request.author(), request.isbn(), request.synopsis(),
                connectedUser.getId(), connectedUser.fullName()
        );
        BookResponse response = bookDtoMapper.toResponse(book);
        URI location = URI.create("/books/" + response.id());
        return ResponseEntity.created(location).body(ApiResponse.of("Book added successfully", response));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<ApiResponse<BookResponse>> findById(@PathVariable Integer bookId) {
        Book book = findBookByIdUseCase.execute(bookId);
        return ResponseEntity.ok(ApiResponse.of("Book retrieved", bookDtoMapper.toResponse(book)));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllDisplayable(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserEntity connectedUser
    ) {
        PageResult<Book> result = findAllDisplayableBooksUseCase.execute(page, size, connectedUser.getId());
        return ResponseEntity.ok(bookDtoMapper.toPageResponse(result));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllByOwner(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserEntity connectedUser
    ) {
        PageResult<Book> result = findAllBooksByOwnerUseCase.execute(page, size, connectedUser.getId());
        return ResponseEntity.ok(bookDtoMapper.toPageResponse(result));
    }

    @PatchMapping("/shareable/{bookId}")
    public ResponseEntity<ApiResponse<BookResponse>> updateShareableStatus(
            @PathVariable Integer bookId,
            @AuthenticationPrincipal UserEntity connectedUser
    ) {
        Book book = updateShareableStatusUseCase.execute(bookId, connectedUser.getId());
        return ResponseEntity.ok(ApiResponse.of("Book status updated successfully", bookDtoMapper.toResponse(book)));
    }

    @PostMapping(value = "/cover/{bookId}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<BookResponse>> uploadBookCoverPicture(
            @PathVariable Integer bookId,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserEntity connectedUser
    ) throws IOException {
        Book book = uploadBookCoverUseCase.execute(bookId, file.getBytes(), file.getOriginalFilename(), connectedUser.getId());
        return ResponseEntity.ok(ApiResponse.of("Image uploaded successfully", bookDtoMapper.toResponse(book)));
    }

}
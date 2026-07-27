package com.nabgha.book.book;

import com.nabgha.book.common.ApiResponse;
import com.nabgha.book.common.PageResponse;
import com.nabgha.book.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Book") // for swagger docs
class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookResponse>> saveBook(
            @Valid @RequestBody BookRequest request,
            @AuthenticationPrincipal User connectedUser
            ) {
        BookResponse book = bookService.save(request, connectedUser);
        URI location = URI.create("/api/v1/books/" + book.id());
        return ResponseEntity.created(location)
                .body(ApiResponse.of("Book added successfully", book));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<ApiResponse<BookResponse>> getBook(@PathVariable("bookId") Integer bookId) {
        return ResponseEntity.ok(ApiResponse.of("Book retrieved successfully", bookService.findById(bookId)));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @AuthenticationPrincipal User connectedUser
    ) {
        return ResponseEntity.ok(bookService.findAllBooks(page, size, connectedUser));
    }
}

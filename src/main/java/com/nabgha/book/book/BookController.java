package com.nabgha.book.book;

import com.nabgha.book.common.ApiResponse;
import com.nabgha.book.common.PageResponse;
import com.nabgha.book.user.User;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(book.id()).toUri();
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

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @AuthenticationPrincipal User connectedUser
    ) {
        return ResponseEntity.ok(bookService.findAllBooksByOwner(page, size, connectedUser));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @AuthenticationPrincipal User connectedUser
    ) {
        return ResponseEntity.ok(bookService.findAllBorrowedBooks(page, size, connectedUser));
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @AuthenticationPrincipal User connectedUser
    ) {
        return ResponseEntity.ok(bookService.findAllReturnedBooks(page, size, connectedUser));
    }

    @PatchMapping("/shareable/{bookId}")
    public ResponseEntity<ApiResponse<BookResponse>> updateShareableStatus(
            @PathVariable("bookId") Integer bookId,
            @AuthenticationPrincipal User connectedUser
    ) {
        return ResponseEntity.ok(ApiResponse.of("Book status updated successfully",bookService.updateShareableStatus(bookId, connectedUser)));
    }

    @PatchMapping("/archived/{bookId}")
    public ResponseEntity<ApiResponse<BookResponse>> updateArchivedStatus(
            @PathVariable("bookId") Integer bookId,
            @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(ApiResponse.of("Book archive status updated", bookService.updateArchivedStatus(bookId, user)));
    }

    @PostMapping("/borrow/{bookId}")
    public ResponseEntity<ApiResponse<BorrowedBookResponse>> borrowBook(
            @PathVariable Integer bookId,
            @AuthenticationPrincipal User connectedUser
    ){
        return ResponseEntity.ok(ApiResponse.of("You borrow this book", bookService.borrowBook(bookId, connectedUser)));
    }

    @PatchMapping("/borrow/return/{bookId}")
    public ResponseEntity<ApiResponse<BorrowedBookResponse>> returnBorrowedBook(
            @PathVariable Integer bookId,
            @AuthenticationPrincipal User connectedUser
    ) {
        return ResponseEntity.ok(ApiResponse.of("Book returned successfully", bookService.returnBorrowedBook(bookId, connectedUser)));
    }

    @PatchMapping("/borrow/return/approve/{bookId}")
    public ResponseEntity<ApiResponse<BorrowedBookResponse>> approveReturnBorrowedBook(
            @PathVariable Integer bookId,
            @AuthenticationPrincipal User connectedUser
    ) {
        return ResponseEntity.ok(ApiResponse.of("Book return approved", bookService.approveReturnBorrowedBook(bookId, connectedUser)));
    }

    @PostMapping(value = "/cover/{bookId}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<?>> uploadBookCoverPicture(
            @PathVariable("bookId") Integer bookId,
            @Parameter()
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal User connectedUser
    ) {
        bookService.uploadBookCoverPicture(file, connectedUser, bookId);
        URI location = URI.create("/books/" + bookId);
        return ResponseEntity.created(location).body(ApiResponse.of("Image uploaded successfully", null));
    }

    @GetMapping("/cover/{bookId}")
    public ResponseEntity<byte[]> downloadBookCoverPicture(
            @PathVariable Integer bookId
    ) {
        byte[] cover = bookService.getBookCover(bookId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(cover);
    }

}

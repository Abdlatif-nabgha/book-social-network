package com.nabgha.book.history.presentation.rest.controller;

import com.nabgha.book.common.domain.PageResult;
import com.nabgha.book.common.presentation.ApiResponse;
import com.nabgha.book.common.presentation.PageResponse;
import com.nabgha.book.history.domain.model.BookTransactionHistory;
import com.nabgha.book.history.domain.usecase.*;
import com.nabgha.book.history.presentation.rest.dto.BorrowedBookResponse;
import com.nabgha.book.history.presentation.rest.mapper.HistoryDtoMapper;
import com.nabgha.book.user.infrastructure.persistence.jpa.entity.UserEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/books")
@Tag(name = "Book Transaction History")
@RequiredArgsConstructor
class HistoryController {

    private final BorrowBookUseCase borrowBookUseCase;
    private final ReturnBorrowedBookUseCase returnBorrowedBookUseCase;
    private final ApproveReturnBookUseCase approveReturnBookUseCase;
    private final FindAllBorrowedBooksUseCase findAllBorrowedBooksUseCase;
    private final FindAllReturnedBooksUseCase findAllReturnedBooksUseCase;
    private final HistoryDtoMapper historyDtoMapper;

    @PostMapping("/borrow/{bookId}")
    public ResponseEntity<ApiResponse<BorrowedBookResponse>> borrowBook(
            @PathVariable Integer bookId,
            @AuthenticationPrincipal UserEntity connectedUser
    ){
        BookTransactionHistory history = borrowBookUseCase.execute(bookId, connectedUser.getId());
        return ResponseEntity
                .ok(ApiResponse.of("You borrowed this book", historyDtoMapper.toResponse(history)));
    }

    @PatchMapping("/borrow/return/{bookId}")
    public ResponseEntity<ApiResponse<BorrowedBookResponse>> returnBorrowedBook(
            @PathVariable Integer bookId,
            @AuthenticationPrincipal UserEntity connectedUser
    ){
        BookTransactionHistory history = returnBorrowedBookUseCase.execute(bookId, connectedUser.getId());
        return ResponseEntity.ok(ApiResponse.of("Book returned successfully", historyDtoMapper.toResponse(history)));
    }

    @PatchMapping("/borrow/return/approve/{bookId}")
    public ResponseEntity<ApiResponse<BorrowedBookResponse>> approveReturnBorrowedBook(
            @PathVariable Integer bookId,
            @AuthenticationPrincipal UserEntity connectedUser
    ){
        BookTransactionHistory history = approveReturnBookUseCase.execute(bookId, connectedUser.getId());
        return ResponseEntity.ok(ApiResponse.of("Book returned approved", historyDtoMapper.toResponse(history)));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserEntity connectedUser
    ){
        PageResult<BookTransactionHistory> result = findAllBorrowedBooksUseCase.execute(
                page, size, connectedUser.getId()
        );
        return ResponseEntity.ok(historyDtoMapper.toPageResponse(result));
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserEntity connectedUser
    ) {
        PageResult<BookTransactionHistory> result = findAllReturnedBooksUseCase.execute(page, size, connectedUser.getId());
        return ResponseEntity.ok(historyDtoMapper.toPageResponse(result));
    }
}

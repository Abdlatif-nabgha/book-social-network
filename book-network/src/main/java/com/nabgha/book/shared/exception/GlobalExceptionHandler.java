package com.nabgha.book.shared.exception;

import com.nabgha.book.auth.domain.exception.ActivationTokenExpiredException;
import com.nabgha.book.auth.domain.exception.ActivationTokenNotFoundException;
import com.nabgha.book.auth.domain.exception.EmailAlreadyExistsException;
import com.nabgha.book.book.domain.exception.BookNotFoundException;
import com.nabgha.book.book.domain.exception.BookOperationNotPermittedException;
import com.nabgha.book.feedback.domain.exception.FeedbackNotFoundException;
import com.nabgha.book.feedback.domain.exception.FeedbackOperationNotPermittedException;
import com.nabgha.book.history.domain.exception.BookTransactionHistoryNotFoundException;
import com.nabgha.book.history.domain.exception.HistoryOperationNotPermittedException;
import com.nabgha.book.user.domain.exception.UserNotFoundException;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exp) {
        log.warn("Account locked: {}", exp.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exp) {
        log.warn("Account disabled: {}", exp.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exp) {
        log.warn("Authentication failed: Bad credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder().error("Login and/or password is incorrect").build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleException(AccessDeniedException exp) {
        log.warn("Access denied: {}", exp.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ExceptionResponse.builder().error("Access denied: You do not have permission to perform this operation").build());
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException exp) {
        log.error("Email messaging failure: {}", exp.getMessage(), exp);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exp) {
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        log.warn("Validation failed: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder().validationErrors(errors).build());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ExceptionResponse> handleException(MaxUploadSizeExceededException exp) {
        log.warn("Max upload size exceeded: {}", exp.getMessage());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ExceptionResponse.builder().error("File size exceeds the maximum allowed limit").build());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleException(EmailAlreadyExistsException exp) {
        log.warn("Email already exists: {}", exp.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(ActivationTokenExpiredException.class)
    public ResponseEntity<ExceptionResponse> handleException(ActivationTokenExpiredException exp) {
        log.warn("Activation token expired: {}", exp.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(ActivationTokenNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(ActivationTokenNotFoundException exp) {
        log.warn("Activation token not found: {}", exp.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(BookNotFoundException exp) {
        log.warn("Book not found: {}", exp.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(BookOperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(BookOperationNotPermittedException exp) {
        log.warn("Book operation not permitted: {}", exp.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(FeedbackNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(FeedbackNotFoundException exp) {
        log.warn("Feedback not found: {}", exp.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(FeedbackOperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(FeedbackOperationNotPermittedException exp) {
        log.warn("Feedback operation not permitted: {}", exp.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(BookTransactionHistoryNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(BookTransactionHistoryNotFoundException exp) {
        log.warn("Transaction history not found: {}", exp.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(HistoryOperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(HistoryOperationNotPermittedException exp) {
        log.warn("History operation not permitted: {}", exp.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(UserNotFoundException exp) {
        log.warn("User not found: {}", exp.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp) {
        log.error("Unhandled server error: {}", exp.getMessage(), exp);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder()
                        .businessErrorDescription("Internal Error, please try again.")
                        .error(exp.getMessage())
                        .build());
    }
}

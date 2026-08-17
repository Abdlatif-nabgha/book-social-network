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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashSet;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exp) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exp) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exp) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder().error("Login and/or password is incorrect").build());
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException exp) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exp) {
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder().validationErrors(errors).build());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ExceptionResponse> handleException(MaxUploadSizeExceededException exp) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ExceptionResponse.builder().error("File size exceeds the maximum allowed limit").build());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleException(EmailAlreadyExistsException exp) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(ActivationTokenExpiredException.class)
    public ResponseEntity<ExceptionResponse> handleException(ActivationTokenExpiredException exp) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(ActivationTokenNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(ActivationTokenNotFoundException exp) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(BookNotFoundException exp) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(BookOperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(BookOperationNotPermittedException exp) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(FeedbackNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(FeedbackNotFoundException exp) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(FeedbackOperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(FeedbackOperationNotPermittedException exp) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(BookTransactionHistoryNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(BookTransactionHistoryNotFoundException exp) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(HistoryOperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(HistoryOperationNotPermittedException exp) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(UserNotFoundException exp) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ExceptionResponse.builder().error(exp.getMessage()).build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp) {
        exp.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder()
                        .businessErrorDescription("Internal Error, please try again.")
                        .error(exp.getMessage())
                        .build());
    }
}

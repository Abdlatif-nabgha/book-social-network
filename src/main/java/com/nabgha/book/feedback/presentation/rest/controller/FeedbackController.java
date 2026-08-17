package com.nabgha.book.feedback.presentation.rest.controller;

import com.nabgha.book.common.domain.PageResult;
import com.nabgha.book.common.presentation.ApiResponse;
import com.nabgha.book.common.presentation.PageResponse;
import com.nabgha.book.feedback.domain.model.Feedback;
import com.nabgha.book.feedback.domain.usecase.*;
import com.nabgha.book.feedback.presentation.rest.dto.*;
import com.nabgha.book.feedback.presentation.rest.mapper.FeedbackDtoMapper;
import com.nabgha.book.user.infrastructure.persistence.jpa.entity.UserEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/feedbacks")
@Tag(name = "Feedback")
public class FeedbackController {

    private final CreateFeedbackUseCase createFeedbackUseCase;
    private final FindAllFeedbacksByBookUseCase findAllFeedbacksByBookUseCase;
    private final UpdateFeedbackUseCase updateFeedbackUseCase;
    private final DeleteFeedbackUseCase deleteFeedbackUseCase;
    private final FeedbackDtoMapper feedbackDtoMapper;

    public FeedbackController(CreateFeedbackUseCase createFeedbackUseCase,
                              FindAllFeedbacksByBookUseCase findAllFeedbacksByBookUseCase,
                              UpdateFeedbackUseCase updateFeedbackUseCase,
                              DeleteFeedbackUseCase deleteFeedbackUseCase,
                              FeedbackDtoMapper feedbackDtoMapper) {
        this.createFeedbackUseCase = createFeedbackUseCase;
        this.findAllFeedbacksByBookUseCase = findAllFeedbacksByBookUseCase;
        this.updateFeedbackUseCase = updateFeedbackUseCase;
        this.deleteFeedbackUseCase = deleteFeedbackUseCase;
        this.feedbackDtoMapper = feedbackDtoMapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FeedbackResponse>> saveFeedback(
            @Valid @RequestBody FeedbackRequest request,
            @AuthenticationPrincipal UserEntity user
    ) {
        Feedback feedback = createFeedbackUseCase.execute(request.note(), request.comment(), request.bookId(), user.getId());
        FeedbackResponse response = feedbackDtoMapper.toResponse(feedback, user.getId());
        URI location = URI.create("/feedbacks/" + response.id());
        return ResponseEntity.created(location).body(ApiResponse.of("Thank you for your feedback", response));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbacksByBook(
            @PathVariable Integer bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserEntity user
    ) {
        PageResult<Feedback> result = findAllFeedbacksByBookUseCase.execute(bookId, page, size);
        return ResponseEntity.ok(feedbackDtoMapper.toPageResponse(result, user.getId()));
    }

    @PatchMapping("/{feedbackId}")
    public ResponseEntity<ApiResponse<FeedbackResponse>> updateFeedback(
            @PathVariable Integer feedbackId,
            @RequestBody FeedbackUpdateRequest request,
            @AuthenticationPrincipal UserEntity user
    ) {
        Feedback feedback = updateFeedbackUseCase.execute(feedbackId, request.note(), request.comment(), user.getId());
        return ResponseEntity.ok(ApiResponse.of("Feedback updated", feedbackDtoMapper.toResponse(feedback, user.getId())));
    }

    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<ApiResponse<Void>> deleteFeedback(
            @PathVariable Integer feedbackId,
            @AuthenticationPrincipal UserEntity user
    ) {
        deleteFeedbackUseCase.execute(feedbackId, user.getId());
        return ResponseEntity.ok(ApiResponse.of("Feedback deleted", null));
    }
}
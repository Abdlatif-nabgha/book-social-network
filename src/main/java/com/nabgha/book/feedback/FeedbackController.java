package com.nabgha.book.feedback;

import com.nabgha.book.common.ApiResponse;
import com.nabgha.book.common.PageResponse;
import com.nabgha.book.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("/feedbacks")
@Tag(name = "Feedback")
@RequiredArgsConstructor
class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<ApiResponse<FeedbackResponse>> saveFeedback(
            @Valid @RequestBody FeedbackRequest request,
            @AuthenticationPrincipal User user
    ){
        FeedbackResponse feedback = feedbackService.save(request, user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(feedback.id()).toUri();

        return ResponseEntity.created(location)
                .body(ApiResponse.of("Thank you for your feedback", feedback));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbacksByBook(
            @PathVariable("bookId") Integer bookId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @AuthenticationPrincipal User user
    ){
        return ResponseEntity.ok(feedbackService.findAllFeedbacksByBook(bookId, page, size, user));
    }

    @PatchMapping("/{feedbackId}")
    public ResponseEntity<ApiResponse<FeedbackResponse>> updateFeedback(
            @PathVariable Integer feedbackId,
            @RequestBody FeedbackUpdateRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(ApiResponse.of("Feedback updated", feedbackService.updateFeedback(feedbackId, request, user)));
    }

    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<ApiResponse<Void>> deleteFeedback(
            @PathVariable Integer feedbackId,
            @AuthenticationPrincipal User user
    ) {
        feedbackService.deleteFeedback(feedbackId, user);
        return ResponseEntity.ok(ApiResponse.of("Feedback deleted", null));
    }
}

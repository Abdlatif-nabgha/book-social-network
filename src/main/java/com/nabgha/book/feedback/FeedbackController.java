package com.nabgha.book.feedback;

import com.nabgha.book.common.ApiResponse;
import com.nabgha.book.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
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
            @RequestBody FeedbackRequest request,
            @AuthenticationPrincipal User user
    ){
        FeedbackResponse feedback = feedbackService.save(request, user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(feedback.id()).toUri();

        return ResponseEntity.created(location)
                .body(ApiResponse.of("Thank you for your feedback", feedback));
    }


}

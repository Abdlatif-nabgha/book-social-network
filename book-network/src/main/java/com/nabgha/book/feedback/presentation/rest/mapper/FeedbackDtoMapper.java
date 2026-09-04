package com.nabgha.book.feedback.presentation.rest.mapper;

import com.nabgha.book.common.domain.PageResult;
import com.nabgha.book.common.presentation.PageResponse;
import com.nabgha.book.feedback.domain.model.Feedback;
import com.nabgha.book.feedback.presentation.rest.dto.FeedbackResponse;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FeedbackDtoMapper {

    public FeedbackResponse toResponse(Feedback feedback, Integer connectedUserId) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .bookId(feedback.getBookId())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(), connectedUserId))
                .build();
    }

    public PageResponse<FeedbackResponse> toPageResponse(PageResult<Feedback> result, Integer connectedUserId) {
        return new PageResponse<>(
                result.content().stream().map(f -> toResponse(f, connectedUserId)).toList(),
                result.pageNumber(),
                result.pageSize(),
                result.totalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }
}
package com.interviewprep.feedback_service.Dto;

import jakarta.validation.constraints.NotNull;

public record FeedbackRequest(
        @NotNull(message = "Helpful field is required")
        Boolean helpful,

        @NotNull(message = "Timestamp is required")
        String timestamp,

        String page
) {}

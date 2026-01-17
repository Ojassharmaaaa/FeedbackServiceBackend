package com.interviewprep.feedback_service.Dto;

public record FeedbackResponse(
        String status,
        String message
) {
    public static FeedbackResponse success() {
        return new FeedbackResponse("success", "Feedback submitted successfully");
    }

    public static FeedbackResponse error(String message) {
        return new FeedbackResponse("error", message);
    }
}
package com.interviewprep.feedback_service.Dto;

public record FeedbackStats(
        Long totalResponses,
        Long yesCount,
        Long noCount,
        Double yesPercentage
) {}
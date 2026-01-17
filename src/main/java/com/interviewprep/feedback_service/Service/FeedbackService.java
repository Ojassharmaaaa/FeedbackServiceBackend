package com.interviewprep.feedback_service.Service;

import com.interviewprep.feedback_service.Dto.FeedbackRequest;
import com.interviewprep.feedback_service.Dto.FeedbackStats;
import com.interviewprep.feedback_service.Repository.FeedbackRepository;
import com.interviewprep.feedback_service.model.Feedback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Transactional
    public Feedback saveFeedback(FeedbackRequest request, String userIp, String userAgent) {
        Feedback feedback = new Feedback();

        feedback.setHelpful(request.helpful());
        feedback.setPage(request.page());

        // Parse timestamp with multiple format support
        LocalDateTime timestamp = parseTimestamp(request.timestamp());
        feedback.setTimestamp(timestamp);

        feedback.setUserIp(userIp);
        feedback.setUserAgent(userAgent);

        Feedback saved = feedbackRepository.save(feedback);
        log.info("Feedback saved: ID={}, Helpful={}, Page={}",
                saved.getId(), saved.getHelpful(), saved.getPage());

        return saved;
    }

    private LocalDateTime parseTimestamp(String timestampStr) {
        if (timestampStr == null || timestampStr.isEmpty()) {
            return LocalDateTime.now();
        }

        // Try multiple formats
        DateTimeFormatter[] formatters = {
                DateTimeFormatter.ISO_DATE_TIME,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(timestampStr, formatter);
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }

        log.warn("Could not parse timestamp: {}, using current time", timestampStr);
        return LocalDateTime.now();
    }

    public FeedbackStats getStatistics() {
        Long total = feedbackRepository.count();
        Long yesCount = feedbackRepository.countHelpfulYes();
        Long noCount = feedbackRepository.countHelpfulNo();

        Double yesPercentage = total > 0
                ? (yesCount * 100.0 / total)
                : 0.0;

        return new FeedbackStats(total, yesCount, noCount, yesPercentage);
    }

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    public List<Feedback> getFeedbackByPage(String page) {
        return feedbackRepository.findByPage(page);
    }
}
package com.interviewprep.feedback_service.Controller;

import com.interviewprep.feedback_service.Dto.FeedbackRequest;
import com.interviewprep.feedback_service.Dto.FeedbackResponse;
import com.interviewprep.feedback_service.Dto.FeedbackStats;

import com.interviewprep.feedback_service.Service.FeedbackService;
import com.interviewprep.feedback_service.model.Feedback;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
@Slf4j
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<FeedbackResponse> submitFeedback(
            @Valid @RequestBody FeedbackRequest request,
            HttpServletRequest httpRequest) {

        try {
            log.info("Received feedback request: {}", request);

            String userIp = getClientIp(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");

            feedbackService.saveFeedback(request, userIp, userAgent);

            return ResponseEntity.ok(FeedbackResponse.success());

        } catch (Exception e) {
            log.error("Error saving feedback: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(FeedbackResponse.error("Failed to save feedback: " + e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<FeedbackStats> getStatistics() {
        FeedbackStats stats = feedbackService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        List<Feedback> feedbackList = feedbackService.getAllFeedback();
        return ResponseEntity.ok(feedbackList);
    }

    @GetMapping("/page/{pageName}")
    public ResponseEntity<List<Feedback>> getFeedbackByPage(@PathVariable String pageName) {
        List<Feedback> feedbackList = feedbackService.getFeedbackByPage(pageName);
        return ResponseEntity.ok(feedbackList);
    }

    // Exception handler for validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("Validation errors: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Handle multiple IPs (take first one)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
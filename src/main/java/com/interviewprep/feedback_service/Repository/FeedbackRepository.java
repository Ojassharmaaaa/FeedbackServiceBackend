package com.interviewprep.feedback_service.Repository;


import com.interviewprep.feedback_service.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Count total feedback
    long count();

    // Count by helpful status
    long countByHelpful(Boolean helpful);

    // Find feedback by page
    List<Feedback> findByPage(String page);

    // Find feedback within date range
    List<Feedback> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    // Custom query for statistics
    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.helpful = true")
    Long countHelpfulYes();

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.helpful = false")
    Long countHelpfulNo();
}

package com.khomishchak.feedbackservice.repository;

import com.khomishchak.feedbackservice.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}

package com.khomishchak.ws.repositories;

import com.khomishchak.ws.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}

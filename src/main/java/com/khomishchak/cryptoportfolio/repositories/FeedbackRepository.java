package com.khomishchak.cryptoportfolio.repositories;

import com.khomishchak.cryptoportfolio.model.Feedback;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}

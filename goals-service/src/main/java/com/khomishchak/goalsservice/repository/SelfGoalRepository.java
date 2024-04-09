package com.khomishchak.goalsservice.repository;

import com.khomishchak.goalsservice.model.SelfGoal;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface SelfGoalRepository extends JpaRepository<SelfGoal, Long> {

    List<SelfGoal> findAllByUserId(Long userId);



    // TODO: add timezones handle logic
    List<SelfGoal> findByClosedIsFalseAndEndDateBefore(LocalDateTime endDate);

    default List<SelfGoal> getAllOverdueGoals() {
        return findByClosedIsFalseAndEndDateBefore(LocalDateTime.now());
    }
}

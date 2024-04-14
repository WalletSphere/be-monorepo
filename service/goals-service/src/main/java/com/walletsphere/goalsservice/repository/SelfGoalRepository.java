package com.walletsphere.goalsservice.repository;

import com.walletsphere.goalsservice.model.SelfGoal;
import org.springframework.data.jpa.repository.JpaRepository;

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

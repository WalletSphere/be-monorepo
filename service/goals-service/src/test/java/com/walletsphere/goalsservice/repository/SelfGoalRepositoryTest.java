package com.walletsphere.goalsservice.repository;

import static com.walletsphere.goalsservice.model.GoalType.DAILY_DEPOSIT_GOAL;
import static com.walletsphere.goalsservice.model.GoalType.MONTHLY_DEPOSIT_GOAL;
import static com.walletsphere.goalsservice.model.GoalType.WEEKLY_DEPOSIT_GOAL;
import com.walletsphere.goalsservice.model.SelfGoal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class SelfGoalRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    SelfGoalRepository selfGoalRepository;

    @BeforeEach
    void setUp() {
        SelfGoal selfGoalDailyNotClosed = SelfGoal.builder().goalType(DAILY_DEPOSIT_GOAL).goalAmount(10)
                .startDate(LocalDateTime.now().minusDays(2)).endDate(LocalDateTime.now().minusDays(1)).closed(false).build();
        SelfGoal selfGoalWeeklyNotClosed = SelfGoal.builder().goalType(WEEKLY_DEPOSIT_GOAL).goalAmount(50)
                .startDate(LocalDateTime.now().minusDays(2)).endDate(LocalDateTime.now().minusDays(1)).closed(false).build();
        SelfGoal selfGoalMonthlyNotClosed = SelfGoal.builder().goalType(MONTHLY_DEPOSIT_GOAL).goalAmount(300)
                .startDate(LocalDateTime.now().minusDays(2)).endDate(LocalDateTime.now().minusDays(1)).closed(false).build();
        SelfGoal selfGoalDailyClosed = SelfGoal.builder().goalType(DAILY_DEPOSIT_GOAL).goalAmount(20)
                .startDate(LocalDateTime.now().minusDays(4)).endDate(LocalDateTime.now().minusDays(3)).closed(true).build();
        SelfGoal selfGoalWeeklyClosed = SelfGoal.builder().goalType(WEEKLY_DEPOSIT_GOAL).goalAmount(100)
                .startDate(LocalDateTime.now().minusDays(4)).endDate(LocalDateTime.now().minusDays(3)).closed(true).build();
        SelfGoal selfGoalMonthlyClosed = SelfGoal.builder().goalType(MONTHLY_DEPOSIT_GOAL).goalAmount(600)
                .startDate(LocalDateTime.now().minusDays(4)).endDate(LocalDateTime.now().minusDays(3)).closed(true).build();
        selfGoalRepository.saveAll(List.of(selfGoalDailyNotClosed, selfGoalWeeklyNotClosed, selfGoalMonthlyNotClosed,
                selfGoalDailyClosed, selfGoalWeeklyClosed, selfGoalMonthlyClosed));
    }

    @Test
    void connectionEstablished() {
        assertTrue(postgres.isCreated());
        assertTrue(postgres.isRunning());
    }

    @Test
    void getAllOverdueGoals() {
        // when
        List<SelfGoal> goals = selfGoalRepository.getAllOverdueGoals();

        // then
        assertEquals(3, goals.size());
        assertEquals(List.of(10.0, 50.0, 300.0), goals.stream().map(SelfGoal::getGoalAmount).toList());
    }
}
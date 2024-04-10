package com.khomishchak.ws.repositories;

import com.khomishchak.ws.model.goals.GoalType;
import com.khomishchak.ws.model.goals.SelfGoal;
import com.khomishchak.ws.services.security.encryption.AesEncryptionService;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SelfGoalRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    SelfGoalRepository selfGoalRepository;

    @MockBean
    AesEncryptionService aesEncryptionService;

    @BeforeEach
    void setUp() {
        SelfGoal selfGoalDailyNotClosed = SelfGoal.builder().goalType(GoalType.DAILY_DEPOSIT_GOAL).goalAmount(10)
                .startDate(LocalDateTime.now().minusDays(2)).endDate(LocalDateTime.now().minusDays(1)).isClosed(false).build();
        SelfGoal selfGoalWeeklyNotClosed = SelfGoal.builder().goalType(GoalType.WEEKLY_DEPOSIT_GOAL).goalAmount(50)
                .startDate(LocalDateTime.now().minusDays(2)).endDate(LocalDateTime.now().minusDays(1)).isClosed(false).build();
        SelfGoal selfGoalMonthlyNotClosed = SelfGoal.builder().goalType(GoalType.MONTHLY_DEPOSIT_GOAL).goalAmount(300)
                .startDate(LocalDateTime.now().minusDays(2)).endDate(LocalDateTime.now().minusDays(1)).isClosed(false).build();
        SelfGoal selfGoalDailyClosed = SelfGoal.builder().goalType(GoalType.DAILY_DEPOSIT_GOAL).goalAmount(20)
                .startDate(LocalDateTime.now().minusDays(4)).endDate(LocalDateTime.now().minusDays(3)).isClosed(true).build();
        SelfGoal selfGoalWeeklyClosed = SelfGoal.builder().goalType(GoalType.WEEKLY_DEPOSIT_GOAL).goalAmount(100)
                .startDate(LocalDateTime.now().minusDays(4)).endDate(LocalDateTime.now().minusDays(3)).isClosed(true).build();
        SelfGoal selfGoalMonthlyClosed = SelfGoal.builder().goalType(GoalType.MONTHLY_DEPOSIT_GOAL).goalAmount(600)
                .startDate(LocalDateTime.now().minusDays(4)).endDate(LocalDateTime.now().minusDays(3)).isClosed(true).build();
        selfGoalRepository.saveAll(List.of(selfGoalDailyNotClosed, selfGoalWeeklyNotClosed, selfGoalMonthlyNotClosed,
                selfGoalDailyClosed, selfGoalWeeklyClosed, selfGoalMonthlyClosed));
    }

    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void getAllOverdueGoals() {
        // when
        List<SelfGoal> goals = selfGoalRepository.getAllOverdueGoals();

        // then
        assertThat(goals.size()).isEqualTo(3);
        assertThat(goals.stream().map(SelfGoal::getGoalAmount).toList()).isEqualTo(List.of(10.0, 50.0, 300.0));
    }

    @AfterEach
    void tearDown() {
        selfGoalRepository.deleteAll();
    }
}
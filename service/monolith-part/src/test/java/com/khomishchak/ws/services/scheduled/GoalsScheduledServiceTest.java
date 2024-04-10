package com.khomishchak.ws.services.scheduled;


import com.khomishchak.ws.model.goals.GoalType;
import com.khomishchak.ws.model.goals.SelfGoal;
import com.khomishchak.ws.repositories.SelfGoalRepository;
import com.khomishchak.ws.services.GoalsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class GoalsScheduledServiceTest {

    private static final long SELF_GOAL_ID = 1L;

    @Mock
    private SelfGoalRepository goalRepository;
    @Mock
    private GoalsService goalsService;

    private GoalsScheduledService scheduledService;

    @BeforeEach
    void setUp() {
        scheduledService = new GoalsScheduledService(goalRepository, goalsService);
    }

    @Test
    void shouldNotCall_whenNoOverdueGoalsArePresent() {
        // given
        when(goalRepository.getAllOverdueGoals()).thenReturn(Collections.emptyList());

        // when
        scheduledService.doAtTheStartOfTheDay();

        // then
        verify(goalRepository, never()).saveAll(any());
    }

    @Test
    void shouldCallSaveAll_whenNoOverdueGoalsArePresent_whenGoalIsNotAchieved() {
        // given
        SelfGoal overdueGoal = createSelfGoal(GoalType.DAILY_DEPOSIT_GOAL, "BTC", 10, 20);
        SelfGoal newGoal = createSelfGoal(GoalType.DAILY_DEPOSIT_GOAL, "BTC", 0, 20);

        overdueGoal.setAchieved(false);
        overdueGoal.setClosed(true);

        newGoal.setStartDate(GoalType.DAILY_DEPOSIT_GOAL.getStartTime(1));
        newGoal.setEndDate(GoalType.DAILY_DEPOSIT_GOAL.getEndTime());
        newGoal.setAchieved(false);
        newGoal.setClosed(false);

        when(goalRepository.getAllOverdueGoals()).thenReturn(List.of(overdueGoal));

        // when
        scheduledService.doAtTheStartOfTheDay();

        // then
        verify(goalRepository, times(1)).saveAll(eq(List.of(overdueGoal, newGoal)));
    }

    @Test
    void shouldCallSaveAll_whenNoOverdueGoalsArePresent_whenGoalIsAchieved() {
        // given
        SelfGoal overdueGoal = createSelfGoal(GoalType.DAILY_DEPOSIT_GOAL, "BTC", 20, 20);
        SelfGoal newGoal = createSelfGoal(GoalType.DAILY_DEPOSIT_GOAL, "BTC", 0, 20);

        overdueGoal.setAchieved(true);
        overdueGoal.setClosed(true);

        newGoal.setStartDate(GoalType.DAILY_DEPOSIT_GOAL.getStartTime(1));
        newGoal.setEndDate(GoalType.DAILY_DEPOSIT_GOAL.getEndTime());
        newGoal.setAchieved(false);
        newGoal.setClosed(false);

        when(goalRepository.getAllOverdueGoals()).thenReturn(List.of(overdueGoal));

        // when
        scheduledService.doAtTheStartOfTheDay();

        // then
        verify(goalRepository, times(1)).saveAll(eq(List.of(overdueGoal, newGoal)));
    }

    private SelfGoal createSelfGoal(GoalType goalType, String ticker, double currentAmount, double goalAmount) {
        return SelfGoal.builder()
                .ticker(ticker)
                .goalType(goalType)
                .currentAmount(currentAmount)
                .goalAmount(goalAmount)
                .startDate(goalType.getStartTime(1))
                .endDate(goalType.getEndTime())
                .build();
    }
}
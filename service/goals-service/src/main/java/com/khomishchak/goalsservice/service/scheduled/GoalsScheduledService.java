package com.khomishchak.goalsservice.service.scheduled;

import com.khomishchak.goalsservice.model.GoalType;
import com.khomishchak.goalsservice.model.SelfGoal;
import com.khomishchak.goalsservice.repository.SelfGoalRepository;

import com.khomishchak.goalsservice.service.GoalsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoalsScheduledService {

    private final GoalsService goalsService;

    public GoalsScheduledService(GoalsService goalsService) {
        this.goalsService = goalsService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void doAtTheStartOfTheDay() {
        List<SelfGoal> overdueGoals = goalsService.getAllOverdueGoals();
        closeOverdueAndCreateNewGoals(overdueGoals);
    }

    private void closeOverdueAndCreateNewGoals(List<SelfGoal> overdueGoals) {
        List<SelfGoal> updatedGoals = new ArrayList<>();
        overdueGoals.forEach(overdueGoal -> {
            closeOverdueGoal(overdueGoal);
            SelfGoal newGoal = createNewFromOverdueGoal(overdueGoal);
            updatedGoals.addAll(List.of(overdueGoal, newGoal));
        });
        goalsService.saveAll(updatedGoals);
    }

    private void closeOverdueGoal(SelfGoal overdueGoal) {
        overdueGoal.setAchieved(goalsService.overdueGoalIsAchieved(overdueGoal));
        overdueGoal.setClosed(true);
    }

    private SelfGoal createNewFromOverdueGoal(SelfGoal overdueGoal) {
        GoalType goalType = overdueGoal.getGoalType();
        return SelfGoal.builder()
                .ticker(overdueGoal.getTicker())
                .userId(overdueGoal.getUserId())
                .goalAmount(overdueGoal.getGoalAmount())
                .goalType(goalType)
                .startDate(goalType.getStartTime(1))
                .endDate(goalType.getEndTime())
                .build();
    }
}

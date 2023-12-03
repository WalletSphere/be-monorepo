package com.khomishchak.ws.services.scheduled;

import com.khomishchak.ws.model.goals.GoalType;
import com.khomishchak.ws.model.goals.SelfGoal;
import com.khomishchak.ws.repositories.SelfGoalRepository;
import com.khomishchak.ws.services.GoalsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalsScheduledService implements ScheduledService {

    private final SelfGoalRepository goalRepository;
    private final GoalsService goalsService;

    public GoalsScheduledService(SelfGoalRepository goalRepository, GoalsService goalsService) {
        this.goalRepository = goalRepository;
        this.goalsService = goalsService;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public void doAtTheStartOfTheDay() {
        List<SelfGoal> overdueGoals = goalRepository.getAllOverdueGoals();
        closeOverdueAndCreateNewGoals(overdueGoals);
    }

    private void closeOverdueAndCreateNewGoals(List<SelfGoal> overdueGoals) {
        overdueGoals.forEach(overdueGoal -> {
            closeOverdueGoal(overdueGoal);
            SelfGoal newGoal = createNewFromOverdueGoal(overdueGoal);
            goalRepository.saveAll(List.of(overdueGoal, newGoal));
        });
    }

    private void closeOverdueGoal(SelfGoal overdueGoal) {
        overdueGoal.setAchieved(goalsService.overdueGoalIsAchieved(overdueGoal));
        overdueGoal.setClosed(true);
    }

    private SelfGoal createNewFromOverdueGoal(SelfGoal overdueGoal) {
        GoalType goalType = overdueGoal.getGoalType();
        return SelfGoal.builder()
                .ticker(overdueGoal.getTicker())
                .user(overdueGoal.getUser())
                .goalAmount(overdueGoal.getGoalAmount())
                .goalType(goalType)
                .startDate(goalType.getStartTime(1))
                .endDate(goalType.getEndTime())
                .build();
    }
}

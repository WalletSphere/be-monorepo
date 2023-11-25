package com.khomishchak.ws.services.scheduled;

import com.khomishchak.ws.model.enums.GoalType;
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

        overdueGoals.forEach(oldGoal -> {
            GoalType goalType = oldGoal.getGoalType();
            SelfGoal newGoal = SelfGoal.builder()
                    .ticker(oldGoal.getTicker())
                    .user(oldGoal.getUser())
                    .goalAmount(oldGoal.getGoalAmount())
                    .goalType(goalType)
                    .startDate(goalType.getStartTime(1))
                    .endDate(goalType.getEndTime())
                    .build();

            oldGoal.setAchieved(goalsService.overdueGoalIsAchieved(oldGoal));
            oldGoal.setClosed(true);

            goalRepository.saveAll(List.of(oldGoal, newGoal));
        });
    }
}

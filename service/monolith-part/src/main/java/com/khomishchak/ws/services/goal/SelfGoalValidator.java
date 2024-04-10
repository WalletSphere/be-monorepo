package com.khomishchak.ws.services.goal;

import com.khomishchak.ws.model.goals.CommonGoalType;
import com.khomishchak.ws.model.goals.SelfGoal;

public interface SelfGoalValidator {

    boolean isAchieved(SelfGoal goal);

    CommonGoalType getCommonGoalType();
}

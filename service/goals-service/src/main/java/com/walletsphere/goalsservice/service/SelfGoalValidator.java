package com.walletsphere.goalsservice.service;


import com.walletsphere.goalsservice.model.CommonGoalType;
import com.walletsphere.goalsservice.model.SelfGoal;

public interface SelfGoalValidator {

    CommonGoalType getCommonGoalType();

    boolean isAchieved(SelfGoal goal);
}

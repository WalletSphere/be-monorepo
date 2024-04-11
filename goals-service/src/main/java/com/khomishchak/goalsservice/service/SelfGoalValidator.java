package com.khomishchak.goalsservice.service;


import com.khomishchak.goalsservice.model.CommonGoalType;
import com.khomishchak.goalsservice.model.SelfGoal;

public interface SelfGoalValidator {

    CommonGoalType getCommonGoalType();

    boolean isAchieved(SelfGoal goal);
}

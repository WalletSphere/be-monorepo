package com.khomishchak.ws.services;

import com.khomishchak.ws.model.goals.CryptoGoalTableTransaction;
import com.khomishchak.ws.model.goals.CryptoGoalsTable;
import com.khomishchak.ws.model.goals.SelfGoal;

import java.util.List;

public interface GoalsService {

    CryptoGoalsTable createCryptoGoalsTable(Long userId, CryptoGoalsTable tableRequest);

    CryptoGoalsTable getCryptoGoalsTable(Long userId);

    CryptoGoalsTable updateCryptoGoalsTable(CryptoGoalsTable cryptoGoalsTable);

    CryptoGoalsTable updateCryptoGoalsTable(CryptoGoalTableTransaction transaction, long tableId);

    List<SelfGoal> getSelfGoals(Long userId);

    List<SelfGoal> createSelfGoals(Long userId, List<SelfGoal> goals);

    boolean overdueGoalIsAchieved(SelfGoal goal);
}

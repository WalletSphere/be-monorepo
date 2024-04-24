package com.walletsphere.goalsservice.service;

import com.walletsphere.goalsservice.model.CryptoGoalTableTransaction;
import com.walletsphere.goalsservice.model.CryptoGoalsTable;
import com.walletsphere.goalsservice.model.SelfGoal;
import com.walletsphere.goalsservice.model.transaction.CreateNewRecordTransaction;

import java.util.List;

public interface GoalsService {

    CryptoGoalsTable createCryptoGoalsTable(Long userId, CryptoGoalsTable tableRequest);

    CryptoGoalsTable getCryptoGoalsTable(Long userId);

    CryptoGoalsTable updateCryptoGoalsTable(CryptoGoalsTable cryptoGoalsTable, long userId);

    CryptoGoalsTable updateCryptoGoalsTable(CryptoGoalTableTransaction transaction, long tableId);

    CryptoGoalsTable updateCryptoGoalsTable(CreateNewRecordTransaction transaction, long tableId);

    List<SelfGoal> getSelfGoals(Long userId);

    List<SelfGoal> createSelfGoals(Long userId, List<SelfGoal> goals);

    List <SelfGoal> saveAll(Iterable<SelfGoal> entities);

    List<SelfGoal> getAllOverdueGoals();

    boolean overdueGoalIsAchieved(SelfGoal goal);
}

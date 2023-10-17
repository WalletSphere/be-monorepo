package com.khomishchak.cryptoportfolio.services;

import com.khomishchak.cryptoportfolio.model.goals.CryptoGoalsRecordUpdateReq;
import com.khomishchak.cryptoportfolio.model.goals.CryptoGoalsTable;
import com.khomishchak.cryptoportfolio.model.goals.SelfGoal;

import java.util.List;

public interface GoalsService {

    CryptoGoalsTable createCryptoGoalsTable(Long userId, CryptoGoalsTable tableRequest);

    CryptoGoalsTable getCryptoGoalsTable(Long userId);

    CryptoGoalsTable updateCryptoGoalsTable(CryptoGoalsTable cryptoGoalsTable);

    CryptoGoalsTable updateCryptoGoalsTableRecords(List<CryptoGoalsRecordUpdateReq> recordUpdateReq, long tableId);

    List<SelfGoal> getSelfGoals(Long userId);

    List<SelfGoal> createSelfGoals(Long userId, List<SelfGoal> goals);

    boolean overdueGoalIsAchieved(SelfGoal goal);
}

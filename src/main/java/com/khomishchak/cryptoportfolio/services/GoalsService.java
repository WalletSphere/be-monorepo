package com.khomishchak.cryptoportfolio.services;

import com.khomishchak.cryptoportfolio.model.goals.CryptoGoalsRecordUpdateReq;
import com.khomishchak.cryptoportfolio.model.goals.CryptoGoalsTable;

import java.util.List;

public interface GoalsService {

    CryptoGoalsTable createCryptoGoalsTable(Long accountId, CryptoGoalsTable tableRequest);

    CryptoGoalsTable getCryptoGoalsTable(Long accountId);

    CryptoGoalsTable updateCryptoGoalsTable(CryptoGoalsTable cryptoGoalsTable);

    CryptoGoalsTable updateCryptoGoalsTableRecords(List<CryptoGoalsRecordUpdateReq> recordUpdateReq, long tableId);
}

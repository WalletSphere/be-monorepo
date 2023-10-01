package com.khomishchak.cryproportfolio.services;

import com.khomishchak.cryproportfolio.model.goals.CryptoGoalsRecordUpdateReq;
import com.khomishchak.cryproportfolio.model.goals.CryptoGoalsTable;

import java.util.List;

public interface GoalsService {

    CryptoGoalsTable createCryptoGoalsTable(Long accountId, CryptoGoalsTable tableRequest);

    CryptoGoalsTable getCryptoGoalsTable(Long accountId);

    CryptoGoalsTable updateCryptoGoalsTable(CryptoGoalsTable cryptoGoalsTable);

    CryptoGoalsTable updateCryptoGoalsTableRecords(List<CryptoGoalsRecordUpdateReq> recordUpdateReq, long tableId);
}

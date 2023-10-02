package com.khomishchak.cryptoportfolio.services.integration.whitebit;

import com.khomishchak.cryptoportfolio.model.DepositWithdrawalTransaction;
import com.khomishchak.cryptoportfolio.model.exchanger.Balance;

import java.util.List;

public interface WhiteBitService {

    Balance getAccountBalance(long accoId);

    List<DepositWithdrawalTransaction> getDepositWithdrawalHistory(long accoId);
}

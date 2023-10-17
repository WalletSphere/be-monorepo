package com.khomishchak.cryptoportfolio.services.integration.whitebit;

import com.khomishchak.cryptoportfolio.model.DepositWithdrawalTransaction;
import com.khomishchak.cryptoportfolio.model.exchanger.Balance;

import java.util.List;

public interface WhiteBitService {

    Balance getAccountBalance(long userId);

    List<DepositWithdrawalTransaction> getDepositWithdrawalHistory(long userId);
}

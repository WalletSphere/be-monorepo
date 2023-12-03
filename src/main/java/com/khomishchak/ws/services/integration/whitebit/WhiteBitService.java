package com.khomishchak.ws.services.integration.whitebit;

import com.khomishchak.ws.model.exchanger.Balance;
import com.khomishchak.ws.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;

public interface WhiteBitService {

    Balance getAccountBalance(long userId);

    ExchangerDepositWithdrawalTransactions getDepositWithdrawalHistory(long userId);
}

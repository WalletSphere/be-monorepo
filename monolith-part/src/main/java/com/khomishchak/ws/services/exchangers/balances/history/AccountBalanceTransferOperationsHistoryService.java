package com.khomishchak.ws.services.exchangers.balances.history;

import com.khomishchak.ws.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;

import java.util.List;

public interface AccountBalanceTransferOperationsHistoryService {

    List<ExchangerDepositWithdrawalTransactions> getDepositWithdrawalTransactionsHistory(long userId);

    List<ExchangerDepositWithdrawalTransactions> synchronizeDepositWithdrawalTransactionsHistory(long userId);

    void deleteDepositWithdrawalTransactionsHistory(long balanceId);
}

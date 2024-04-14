package com.walletsphere.wsmonolith.services.exchangers.balances.history;

import com.walletsphere.wsmonolith.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;

import java.util.List;

public interface AccountBalanceTransferOperationsHistoryService {

    List<ExchangerDepositWithdrawalTransactions> getDepositWithdrawalTransactionsHistory(long userId);

    List<ExchangerDepositWithdrawalTransactions> synchronizeDepositWithdrawalTransactionsHistory(long userId);

    void deleteDepositWithdrawalTransactionsHistory(long balanceId);
}

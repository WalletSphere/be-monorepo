package com.walletsphere.wsmonolith.services.exchangers.balances.cache;

import com.walletsphere.wsmonolith.services.exchangers.balances.history.AccountBalanceTransferOperationsHistoryService;
import org.springframework.stereotype.Service;

@Service
public class BalanceCacheHandler {

    private final AccountBalanceTransferOperationsHistoryService accountBalanceTransferOperationsHistoryService;

    public BalanceCacheHandler(AccountBalanceTransferOperationsHistoryService accountBalanceTransferOperationsHistoryService) {
        this.accountBalanceTransferOperationsHistoryService = accountBalanceTransferOperationsHistoryService;
    }

    public void deleteAllBalanceRelatedCacheInfo(long balanceId) {
        accountBalanceTransferOperationsHistoryService.deleteDepositWithdrawalTransactionsHistory(balanceId);
    }
}

package com.walletsphere.wsmonolith.services.exchangers.balances.history;

import com.walletsphere.wsmonolith.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import com.walletsphere.wsmonolith.repositories.DepositWithdrawalTransactionsHistoryRepository;
import com.walletsphere.wsmonolith.services.UserService;
import com.walletsphere.wsmonolith.services.exchangers.ExchangerConnectorServiceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(value = "ws.account.balance.operations.history.service.type", havingValue = "remote")
public class RemoteAccountBalanceTransferOperationsHistoryService extends CommonAccountBalanceTransferOperationsHistoryService {

    public RemoteAccountBalanceTransferOperationsHistoryService(UserService userService,
                                                                List<ExchangerConnectorServiceFactory> exchangerServiceFactories,
                                                                DepositWithdrawalTransactionsHistoryRepository depositWithdrawalTransactionsHistoryRepository) {
        super(userService, exchangerServiceFactories, depositWithdrawalTransactionsHistoryRepository);
    }

    @Override
    public List<ExchangerDepositWithdrawalTransactions> getDepositWithdrawalTransactionsHistory(long userId) {
        return super.synchronizeDepositWithdrawalTransactionsHistory(userId);
    }
}

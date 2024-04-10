package com.khomishchak.ws.services.exchangers.balances.history;

import com.khomishchak.ws.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import com.khomishchak.ws.repositories.DepositWithdrawalTransactionsHistoryRepository;
import com.khomishchak.ws.services.UserService;
import com.khomishchak.ws.services.exchangers.ExchangerConnectorServiceFactory;
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

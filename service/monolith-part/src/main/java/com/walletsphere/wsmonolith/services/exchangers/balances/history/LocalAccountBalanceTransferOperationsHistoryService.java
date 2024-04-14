package com.walletsphere.wsmonolith.services.exchangers.balances.history;

import com.walletsphere.wsmonolith.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import com.walletsphere.wsmonolith.repositories.DepositWithdrawalTransactionsHistoryRepository;
import com.walletsphere.wsmonolith.services.UserService;
import com.walletsphere.wsmonolith.services.exchangers.ExchangerConnectorServiceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(value = "ws.account.balance.operations.history.service.type", havingValue = "local")
public class LocalAccountBalanceTransferOperationsHistoryService extends CommonAccountBalanceTransferOperationsHistoryService {

    private final DepositWithdrawalTransactionsHistoryRepository depositWithdrawalTransactionsHistoryRepository;

    public LocalAccountBalanceTransferOperationsHistoryService(UserService userService,
                                                               List<ExchangerConnectorServiceFactory> exchangerServiceFactories,
                                                               DepositWithdrawalTransactionsHistoryRepository depositWithdrawalTransactionsHistoryRepository) {
        super(userService, exchangerServiceFactories, depositWithdrawalTransactionsHistoryRepository);
        this.depositWithdrawalTransactionsHistoryRepository = depositWithdrawalTransactionsHistoryRepository;
    }

    @Override
    @Cacheable(value = "depositWithdrawalTransactionHistoryCache", key = "#userId")
    public List<ExchangerDepositWithdrawalTransactions> getDepositWithdrawalTransactionsHistory(long userId) {
        return depositWithdrawalTransactionsHistoryRepository.findAllByUserId(userId);
    }

}

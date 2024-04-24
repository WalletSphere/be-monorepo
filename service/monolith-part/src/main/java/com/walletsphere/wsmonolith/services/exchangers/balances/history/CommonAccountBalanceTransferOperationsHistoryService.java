package com.walletsphere.wsmonolith.services.exchangers.balances.history;

import com.walletsphere.wsmonolith.model.enums.ExchangerCode;
import com.walletsphere.wsmonolith.model.exchanger.ApiKeySetting;
import com.walletsphere.wsmonolith.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import com.walletsphere.wsmonolith.repositories.DepositWithdrawalTransactionsHistoryRepository;
import com.walletsphere.wsmonolith.services.UserService;
import com.walletsphere.wsmonolith.services.exchangers.ExchangerConnectorService;
import com.walletsphere.wsmonolith.services.exchangers.ExchangerConnectorServiceFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public abstract class CommonAccountBalanceTransferOperationsHistoryService implements AccountBalanceTransferOperationsHistoryService {

    private final UserService userService;
    private final Map<ExchangerCode, ExchangerConnectorServiceFactory> exchangerServiceFactories;
    private final DepositWithdrawalTransactionsHistoryRepository depositWithdrawalTransactionsHistoryRepository;


    public CommonAccountBalanceTransferOperationsHistoryService(UserService userService,
                                                                List<ExchangerConnectorServiceFactory> exchangerServiceFactories,
                                                                DepositWithdrawalTransactionsHistoryRepository depositWithdrawalTransactionsHistoryRepository) {
        this.userService = userService;
        this.exchangerServiceFactories = exchangerServiceFactories.stream()
                .collect(Collectors.toMap(ExchangerConnectorServiceFactory::getExchangerCode, factory -> factory));
        this.depositWithdrawalTransactionsHistoryRepository = depositWithdrawalTransactionsHistoryRepository;
    }

    @Override
    @Transactional
    @CachePut(value = "depositWithdrawalTransactionHistoryCache", key = "#userId")
    public List<ExchangerDepositWithdrawalTransactions> synchronizeDepositWithdrawalTransactionsHistory(long userId) {
        List<ExchangerDepositWithdrawalTransactions> transactions = synchronizeDepositWithdrawalTransactionsFrameWork(userId);
        return depositWithdrawalTransactionsHistoryRepository.saveAll(transactions);
    }

    private List<ExchangerDepositWithdrawalTransactions> synchronizeDepositWithdrawalTransactionsFrameWork(long userId) {
        List<ExchangerDepositWithdrawalTransactions> allTransactions = new ArrayList<>();

        userService.getUserById(userId).getApiKeysSettings().stream()
                .map(ApiKeySetting::getCode)
                .forEach(code -> {
                    allTransactions.add(getExchangerDepositWithdrawalTransactionsForSingleExchanger(code, userId));
                });

        return allTransactions;
    }

    @Override
    @CacheEvict(value = "depositWithdrawalTransactionHistoryCache", key = "#balanceId")
    public void deleteDepositWithdrawalTransactionsHistory(long balanceId) {
        depositWithdrawalTransactionsHistoryRepository.deleteAllByBalance_Id(balanceId);
    }

    private ExchangerDepositWithdrawalTransactions getExchangerDepositWithdrawalTransactionsForSingleExchanger(ExchangerCode code, long userId) {
        return getExchangerConnectorService(code).getDepositWithdrawalHistory(userId);
    }

    ExchangerConnectorService getExchangerConnectorService(ExchangerCode exchangerCode) {
        return exchangerServiceFactories.get(exchangerCode).newInstance();
    }
}

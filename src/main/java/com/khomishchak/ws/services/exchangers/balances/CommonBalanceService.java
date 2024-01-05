package com.khomishchak.ws.services.exchangers.balances;

import com.khomishchak.ws.exceptions.BalanceNotFoundException;
import com.khomishchak.ws.model.User;
import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.model.exchanger.ApiKeySetting;
import com.khomishchak.ws.model.exchanger.Balance;
import com.khomishchak.ws.model.exchanger.ExchangerUniqueCurrenciesDTO;
import com.khomishchak.ws.repositories.BalanceRepository;
import com.khomishchak.ws.services.UserService;
import com.khomishchak.ws.services.exchangers.ExchangerConnectorService;
import com.khomishchak.ws.services.exchangers.ExchangerConnectorServiceFactory;
import com.khomishchak.ws.services.exchangers.balances.cache.BalanceCacheHandler;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public abstract class CommonBalanceService implements BalanceService {

    private final BalanceRepository balanceRepository;
    private final UserService userService;
    private final Map<ExchangerCode, ExchangerConnectorServiceFactory> exchangerServiceFactories;
    private final BalanceCacheHandler balanceCacheHandler;


    public CommonBalanceService(BalanceRepository balanceRepository, UserService userService,
                                List<ExchangerConnectorServiceFactory> exchangerServiceFactories,
                                BalanceCacheHandler balanceCacheHandler) {
            this.balanceRepository = balanceRepository;
            this.userService = userService;
            this.exchangerServiceFactories = exchangerServiceFactories.stream()
                    .collect(Collectors.toMap(ExchangerConnectorServiceFactory::getExchangerCode, factory -> factory));
            this.balanceCacheHandler = balanceCacheHandler;
        }

    // No need to put in cache, because it will be synchronised first, and it will be saved in cache at that stage
    @Override
    public Balance registerBalanceEntryInfo(ExchangerCode code, String balanceName, User user) {
        Balance emptyBalance = Balance.builder()
                .code(code)
                .balanceName(balanceName)
                .user(user)
                .build();

        return balanceRepository.save(emptyBalance);
    }

    @Override
    @Transactional
    @CachePut(value = "balanceCache", key = "#userId")
    public List<Balance> synchronizeBalances(long userId) {
        List<Balance> balances = synchronizeBalancesFrameWork(userId);
        balanceRepository.saveAll(balances);
        return balances;
    }

    @Override
    @Transactional
    public void deleteBalance(long balanceId) {
        deleteBalanceFromCache(balanceId);
    }

    @CacheEvict(value = "balanceCache", key = "#balanceId")
    public void deleteBalanceFromCache(long balanceId) {
        balanceRepository.deleteById(balanceId);
        clearWholeBalanceCache(balanceId);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void clearWholeBalanceCache(Long balanceId) {
        balanceCacheHandler.deleteAllBalanceRelatedCacheInfo(balanceId);
    }

    @Override
    public List<ExchangerUniqueCurrenciesDTO> getUsedCurrencies() {
        return balanceRepository.getAllUsedCurrencyCodes();
    }

    private List<Balance> synchronizeBalancesFrameWork(long userId) {
        List<Balance> balances = new ArrayList<>();

        userService.getUserById(userId).getApiKeysSettings().stream()
                .map(ApiKeySetting::getCode)
                .forEach(code -> {
                    Balance balance = getExchangerConnectorService(code).getMainBalance(userId);
                    balances.add(balance);
                });

        return balances;
    }

    ExchangerConnectorService getExchangerConnectorService(ExchangerCode exchangerCode) {
        return exchangerServiceFactories.get(exchangerCode).newInstance();
    }

    Balance getBalanceByCodeAndUserIdOrThrow(Long userId, ExchangerCode code) {
        return balanceRepository.findByCodeAndUser_Id(code, userId)
                .orElseThrow(() -> new BalanceNotFoundException(String.format("User with id: %d, do not have balance for exchanger: %s", userId, code)));
    }
}

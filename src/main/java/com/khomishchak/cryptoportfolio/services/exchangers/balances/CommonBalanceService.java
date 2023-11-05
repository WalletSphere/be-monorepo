package com.khomishchak.cryptoportfolio.services.exchangers.balances;

import com.khomishchak.cryptoportfolio.exceptions.BalanceNotFoundException;
import com.khomishchak.cryptoportfolio.model.User;
import com.khomishchak.cryptoportfolio.model.enums.ExchangerCode;
import com.khomishchak.cryptoportfolio.model.exchanger.ApiKeySetting;
import com.khomishchak.cryptoportfolio.model.exchanger.Balance;
import com.khomishchak.cryptoportfolio.repositories.BalanceRepository;
import com.khomishchak.cryptoportfolio.services.UserService;
import com.khomishchak.cryptoportfolio.services.exchangers.ExchangerConnectorService;
import com.khomishchak.cryptoportfolio.services.exchangers.ExchangerConnectorServiceFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public abstract class CommonBalanceService implements BalanceService {

    private final BalanceRepository balanceRepository;
    private final UserService userService;
    private final BalancePricingService balancePricingService;
    private final Map<ExchangerCode, ExchangerConnectorServiceFactory> exchangerServiceFactories;


    public CommonBalanceService(BalanceRepository balanceRepository, UserService userService,
                                List<ExchangerConnectorServiceFactory> exchangerServiceFactories,
                                BalancePricingService balancePricingService) {
        this.balanceRepository = balanceRepository;
        this.userService = userService;
        this.exchangerServiceFactories = exchangerServiceFactories.stream()
                .collect(Collectors.toMap(ExchangerConnectorServiceFactory::getExchangerCode, factory -> factory));
        this.balancePricingService = balancePricingService;
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
        balancePricingService.calculateBalancesValuesUpToDate(balances);
        return balances;
    }

    @Override
    @CacheEvict(value = "balanceCache", key = "{#userId, #exchangerCode}")
    public Balance removeBalance(long userId, ExchangerCode exchangerCode) {
        return balanceRepository.deleteByUser_IdAndCode(userId, exchangerCode);
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

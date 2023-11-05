package com.khomishchak.cryptoportfolio.services.exchangers.balances;

import com.khomishchak.cryptoportfolio.model.enums.ExchangerCode;
import com.khomishchak.cryptoportfolio.model.exchanger.Balance;
import com.khomishchak.cryptoportfolio.repositories.BalanceRepository;
import com.khomishchak.cryptoportfolio.services.UserService;
import com.khomishchak.cryptoportfolio.services.exchangers.ExchangerConnectorServiceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(value = "ws.balance.service.type", havingValue = "local")
public class LocalBalanceService extends CommonBalanceService {

    private final BalanceRepository balanceRepository;

    private final BalancePricingService balancePricingService;

    public LocalBalanceService(BalanceRepository balanceRepository, UserService userService,
                               List<ExchangerConnectorServiceFactory> exchangerServiceFactories,
                               BalancePricingService balancePricingService) {
        super(balanceRepository, userService, exchangerServiceFactories, balancePricingService);
        this.balanceRepository = balanceRepository;
        this.balancePricingService = balancePricingService;
    }

    @Override
    @Cacheable(value = "balanceCache", key = "#userId")
    public List<Balance> getMainBalances(long userId) {
        List<Balance> balances = balanceRepository.findAllByUser_Id(userId);
        // if app restarted and cache is empty
        balancePricingService.calculateBalancesValuesUpToDate(balances);
        return balances;
    }

    @Override
    @Cacheable(value = "balanceCache", key="{#userId, #code}")
    public Balance getMainBalance(long userId, ExchangerCode code) {
        return super.getBalanceByCodeAndUserIdOrThrow(userId, code);
    }

}

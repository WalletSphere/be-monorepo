package com.walletsphere.wsmonolith.services.exchangers.balances;

import com.walletsphere.wsmonolith.model.enums.ExchangerCode;
import com.walletsphere.wsmonolith.model.exchanger.Balance;
import com.walletsphere.wsmonolith.repositories.BalanceRepository;
import com.walletsphere.wsmonolith.services.UserService;
import com.walletsphere.wsmonolith.services.exchangers.ExchangerConnectorServiceFactory;
import com.walletsphere.wsmonolith.services.exchangers.balances.cache.BalanceCacheHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(value = "ws.balance.service.type", havingValue = "local")
public class LocalBalanceService extends CommonBalanceService {

    private final BalanceRepository balanceRepository;

    public LocalBalanceService(BalanceRepository balanceRepository, UserService userService,
                               List<ExchangerConnectorServiceFactory> exchangerServiceFactories,
                               BalanceCacheHandler balanceCacheHandler) {
        super(balanceRepository, userService, exchangerServiceFactories, balanceCacheHandler);
        this.balanceRepository = balanceRepository;
    }

    @Override
    @Cacheable(value = "balanceCache", key = "#userId")
    public List<Balance> getMainBalances(long userId) {
        return balanceRepository.findAllByUser_Id(userId);
    }

    @Override
    @Cacheable(value = "balanceCache", key="{#userId, #code}")
    public Balance getMainBalance(long userId, ExchangerCode code) {
        return super.getBalanceByCodeAndUserIdOrThrow(userId, code);
    }

}

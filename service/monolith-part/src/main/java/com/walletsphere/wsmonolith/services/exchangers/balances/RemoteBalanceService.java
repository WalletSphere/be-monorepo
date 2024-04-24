package com.walletsphere.wsmonolith.services.exchangers.balances;

import com.walletsphere.wsmonolith.model.enums.ExchangerCode;
import com.walletsphere.wsmonolith.model.exchanger.Balance;
import com.walletsphere.wsmonolith.repositories.BalanceRepository;
import com.walletsphere.wsmonolith.services.UserService;
import com.walletsphere.wsmonolith.services.exchangers.ExchangerConnectorService;
import com.walletsphere.wsmonolith.services.exchangers.ExchangerConnectorServiceFactory;
import com.walletsphere.wsmonolith.services.exchangers.apikeys.ApiKeySettingService;
import com.walletsphere.wsmonolith.services.exchangers.balances.cache.BalanceCacheHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(value = "ws.balance.service.type", havingValue = "remote")
public class RemoteBalanceService extends CommonBalanceService {

    public RemoteBalanceService(BalanceRepository balanceRepository,
                                List<ExchangerConnectorServiceFactory> exchangerServiceFactories,
                                UserService userService, BalanceCacheHandler balanceCacheHandler,
                                ApiKeySettingService apiKeySettingService) {
        super(balanceRepository, userService, exchangerServiceFactories, balanceCacheHandler, apiKeySettingService);
    }

    @Override
    public List<Balance> getMainBalances(long userId) {
        return super.synchronizeBalances(userId);
    }

    @Override
    public Balance getMainBalance(long userId, ExchangerCode exchangerCode) {
        ExchangerConnectorService exchangerConnectorService = super.getExchangerConnectorService(exchangerCode);
        return exchangerConnectorService.getMainBalance(userId);
    }
}

package com.khomishchak.ws.services.exchangers.balances;

import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.model.exchanger.Balance;
import com.khomishchak.ws.repositories.BalanceRepository;
import com.khomishchak.ws.services.UserService;
import com.khomishchak.ws.services.exchangers.ExchangerConnectorService;
import com.khomishchak.ws.services.exchangers.ExchangerConnectorServiceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(value = "ws.balance.service.type", havingValue = "remote")
public class RemoteBalanceService extends CommonBalanceService {

    public RemoteBalanceService(BalanceRepository balanceRepository,
                                List<ExchangerConnectorServiceFactory> exchangerServiceFactories, UserService userService) {
        super(balanceRepository, userService, exchangerServiceFactories);
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

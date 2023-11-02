package com.khomishchak.cryptoportfolio.services.exchangers.balances;

import com.khomishchak.cryptoportfolio.exceptions.BalanceNotFoundException;
import com.khomishchak.cryptoportfolio.model.User;
import com.khomishchak.cryptoportfolio.model.enums.ExchangerCode;
import com.khomishchak.cryptoportfolio.model.exchanger.Balance;
import com.khomishchak.cryptoportfolio.model.exchanger.Currency;
import com.khomishchak.cryptoportfolio.repositories.BalanceRepository;
import com.khomishchak.cryptoportfolio.services.markets.MarketService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalancePricingService balancePricingService;


    public BalanceServiceImpl(BalanceRepository balanceRepository, BalancePricingService balancePricingService) {
        this.balanceRepository = balanceRepository;
        this.balancePricingService = balancePricingService;
    }

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
    public Balance getBalanceByCodeAndUserId(ExchangerCode code, Long userId, List<Currency> availableCurrencies) {

        Balance balance = balanceRepository.findByCodeAndUser_Id(code, userId)
                .orElseThrow(() -> new BalanceNotFoundException(String.format("User with id: %d, do not have balance for exchanger: %s", userId, code)));
        balance.setCurrencies(availableCurrencies);

        Balance persistedBalance = balanceRepository.save(balance);
        balancePricingService.calculateBalanceValueUpToDate(persistedBalance, availableCurrencies);
        return persistedBalance;
    }
}

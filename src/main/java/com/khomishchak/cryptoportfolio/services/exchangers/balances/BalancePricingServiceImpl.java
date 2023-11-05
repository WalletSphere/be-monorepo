package com.khomishchak.cryptoportfolio.services.exchangers.balances;

import com.khomishchak.cryptoportfolio.model.exchanger.Balance;
import com.khomishchak.cryptoportfolio.model.exchanger.Currency;
import com.khomishchak.cryptoportfolio.services.markets.MarketService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BalancePricingServiceImpl implements BalancePricingService {

    private final MarketService marketService;

    public BalancePricingServiceImpl(MarketService marketService) {
        this.marketService = marketService;
    }

    @Override
    public void calculateBalanceValueUpToDate(Balance balance) {
        Map<String, Double> marketValues = marketService.getCurrentMarketValues();
        handleBalanceTotalValueCalculation(balance, marketValues);
    }

    @Override
    public void calculateBalancesValuesUpToDate(List<Balance> balances) {
        Map<String, Double> marketValues = marketService.getCurrentMarketValues();
        balances.forEach(balance -> handleBalanceTotalValueCalculation(balance, marketValues));
    }

    private void handleBalanceTotalValueCalculation(Balance balance, Map<String, Double> marketValues) {
        Map<String, Currency> currencyMap = balance.getCurrencies().stream()
                .collect(Collectors.toMap(Currency::getCurrencyCode, Function.identity()));


        double totalValue = 0;
        for (Map.Entry<String, Double> entry : marketValues.entrySet()) {
            Currency currency = currencyMap.get(entry.getKey());
            if (currency != null) {
                double currencyTotalValue = entry.getValue() * currency.getAmount();
                currency.setTotalValue(currencyTotalValue);
                totalValue += currencyTotalValue;
            }
        }

        balance.setTotalValue(totalValue);
    }

}

package com.khomishchak.cryptoportfolio.services.exchangers.balances;

import com.khomishchak.cryptoportfolio.model.exchanger.Balance;
import com.khomishchak.cryptoportfolio.model.exchanger.Currency;

import java.util.List;

public interface BalancePricingService {

    void calculateBalanceValueUpToDate(Balance balance, List<Currency> availableCurrencies);
}

package com.khomishchak.cryptoportfolio.services.exchangers.balances;

import com.khomishchak.cryptoportfolio.model.exchanger.Balance;

import java.util.List;

public interface BalancePricingService {

    void calculateBalanceValueUpToDate(Balance balance);

    void calculateBalancesValuesUpToDate(List<Balance> balances);
}

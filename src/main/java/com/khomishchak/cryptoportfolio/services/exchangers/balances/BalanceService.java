package com.khomishchak.cryptoportfolio.services.exchangers.balances;

import com.khomishchak.cryptoportfolio.model.User;
import com.khomishchak.cryptoportfolio.model.enums.ExchangerCode;
import com.khomishchak.cryptoportfolio.model.exchanger.Balance;
import com.khomishchak.cryptoportfolio.model.exchanger.Currency;

import java.util.List;

public interface BalanceService {

    Balance registerBalanceEntryInfo(ExchangerCode code, String balanceName, User user);

    Balance getBalanceByCodeAndUserId(ExchangerCode code, Long userId, List<Currency> availableCurrencies);
}

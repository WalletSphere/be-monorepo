package com.khomishchak.cryptoportfolio.services.exchangers.balances;

import com.khomishchak.cryptoportfolio.model.User;
import com.khomishchak.cryptoportfolio.model.enums.ExchangerCode;
import com.khomishchak.cryptoportfolio.model.exchanger.Balance;

import java.util.List;

public interface BalanceService {

    Balance registerBalanceEntryInfo(ExchangerCode code, String balanceName, User user);
    List<Balance> getMainBalances(long userId);
    Balance getMainBalance(long userId, ExchangerCode exchangerCode);
    Balance removeBalance(long userId, ExchangerCode exchangerCode);
    List<Balance> synchronizeBalances(long userId);
}

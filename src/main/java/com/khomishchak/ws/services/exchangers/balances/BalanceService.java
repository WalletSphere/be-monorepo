package com.khomishchak.ws.services.exchangers.balances;

import com.khomishchak.ws.model.User;
import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.model.exchanger.Balance;

import java.util.List;

public interface BalanceService {

    Balance registerBalanceEntryInfo(ExchangerCode code, String balanceName, User user);
    List<Balance> getMainBalances(long userId);
    Balance getMainBalance(long userId, ExchangerCode exchangerCode);
    Balance removeBalance(long userId, ExchangerCode exchangerCode);
    List<Balance> synchronizeBalances(long userId);
}

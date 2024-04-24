package com.walletsphere.wsmonolith.services.exchangers.balances;

import com.walletsphere.wsmonolith.model.User;
import com.walletsphere.wsmonolith.model.enums.ExchangerCode;
import com.walletsphere.wsmonolith.model.exchanger.Balance;
import com.walletsphere.wsmonolith.model.exchanger.ExchangerUniqueCurrenciesDTO;

import java.util.List;

public interface BalanceService {

    Balance registerBalanceEntryInfo(ExchangerCode code, String balanceName, User user);
    List<Balance> getMainBalances(long userId);
    Balance getMainBalance(long userId, ExchangerCode exchangerCode);
    void deleteBalance(long balanceId);
    List<Balance> synchronizeBalances(long userId);
    List<ExchangerUniqueCurrenciesDTO> getUsedCurrencies();
}

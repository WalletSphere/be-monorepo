package com.walletsphere.wsmonolith.services.exchangers.balances;

import com.walletsphere.wsmonolith.model.enums.ExchangerCode;
import com.walletsphere.wsmonolith.model.exchanger.Balance;
import com.walletsphere.wsmonolith.model.exchanger.ExchangerUniqueCurrenciesDTO;
import com.walletsphere.wsmonolith.model.requests.RegisterExchangerInfoReq;

import java.util.List;

public interface BalanceService {

    Balance registerBalanceEntryInfo(RegisterExchangerInfoReq exchangerInfoReq, Long userId);
    List<Balance> getMainBalances(long userId);
    Balance getMainBalance(long userId, ExchangerCode exchangerCode);
    void deleteBalance(long balanceId);
    List<Balance> synchronizeBalances(long userId);
    List<ExchangerUniqueCurrenciesDTO> getUsedCurrencies();
}

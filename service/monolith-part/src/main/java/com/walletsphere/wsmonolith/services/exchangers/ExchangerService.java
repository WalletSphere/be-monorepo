package com.walletsphere.wsmonolith.services.exchangers;

import com.walletsphere.wsmonolith.model.enums.ExchangerCode;
import com.walletsphere.wsmonolith.model.exchanger.Balance;
import com.walletsphere.wsmonolith.model.exchanger.ExchangerUniqueCurrenciesDTO;
import com.walletsphere.wsmonolith.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import com.walletsphere.wsmonolith.model.requests.RegisterExchangerInfoReq;
import com.walletsphere.wsmonolith.model.response.FirstlyGeneratedBalanceResp;
import com.walletsphere.wsmonolith.model.filter.TransactionSearchCriteria;

import java.util.List;


public interface ExchangerService {

    FirstlyGeneratedBalanceResp addGeneralExchangerInfo(RegisterExchangerInfoReq exchangerInfoReq, long userId);

    Balance getMainBalance(long userId, ExchangerCode code);

    List<Balance> getAllMainBalances(long userId);

    double getDepositValueForPeriod(long userId, TransactionSearchCriteria searchCriteria);

    List<ExchangerDepositWithdrawalTransactions> getWithdrawalDepositWalletHistory(long userId);

    List<Balance> synchronizeBalanceDataForUser(long userId);
    List<ExchangerDepositWithdrawalTransactions> synchronizeDepositWithdrawalTransactionsData(long userId);

    void deleteExchangerForUser(long balanceId);

    List<ExchangerUniqueCurrenciesDTO> getUsedCurrencies();
}
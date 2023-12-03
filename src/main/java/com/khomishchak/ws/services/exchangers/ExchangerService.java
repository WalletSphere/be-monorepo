package com.khomishchak.ws.services.exchangers;

import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.model.exchanger.Balance;
import com.khomishchak.ws.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import com.khomishchak.ws.model.requests.RegisterExchangerInfoReq;
import com.khomishchak.ws.model.response.FirstlyGeneratedBalanceResp;
import com.khomishchak.ws.model.response.SyncBalancesResp;
import com.khomishchak.ws.model.response.SyncDepositWithdrawalTransactionsResp;

import java.util.List;


public interface ExchangerService {

    FirstlyGeneratedBalanceResp addGeneralExchangerInfo(RegisterExchangerInfoReq exchangerInfoReq, long userId);

    Balance getMainBalance(long userId, ExchangerCode code);

    List<Balance> getAllMainBalances(long userId);

    List<ExchangerDepositWithdrawalTransactions> getWithdrawalDepositWalletHistory(long userId);

    SyncBalancesResp synchronizeBalanceDataForUser(long userId);
    SyncDepositWithdrawalTransactionsResp synchronizeDepositWithdrawalTransactionsData(long userId);

    void deleteExchangerForUser(long balanceId);
}
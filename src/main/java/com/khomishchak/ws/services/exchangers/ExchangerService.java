package com.khomishchak.ws.services.exchangers;

import com.khomishchak.ws.model.DepositWithdrawalTransaction;
import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.model.exchanger.Balance;
import com.khomishchak.ws.model.requests.RegisterExchangerInfoReq;
import com.khomishchak.ws.model.response.DeleteExchangerResp;
import com.khomishchak.ws.model.response.FirstlyGeneratedBalanceResp;
import com.khomishchak.ws.model.response.SyncDataResp;

import java.util.List;


public interface ExchangerService {

    FirstlyGeneratedBalanceResp addGeneralExchangerInfo(RegisterExchangerInfoReq exchangerInfoReq, long userId);

    Balance getMainBalance(long userId, ExchangerCode code);

    List<Balance> getAllMainBalances(long userId);

    List<DepositWithdrawalTransaction> getWithdrawalDepositWalletHistory(long userId, ExchangerCode exchangerCode);

    SyncDataResp synchronizeBalanceDataForUser(long userId);

    DeleteExchangerResp deleteExchangerForUser(long userId, ExchangerCode code);
}

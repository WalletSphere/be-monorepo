package com.khomishchak.cryptoportfolio.services.exchangers;

import com.khomishchak.cryptoportfolio.model.DepositWithdrawalTransaction;
import com.khomishchak.cryptoportfolio.model.enums.ExchangerCode;
import com.khomishchak.cryptoportfolio.model.exchanger.Balance;
import com.khomishchak.cryptoportfolio.model.requests.RegisterExchangerInfoReq;
import com.khomishchak.cryptoportfolio.model.response.DeleteExchangerResp;
import com.khomishchak.cryptoportfolio.model.response.FirstlyGeneratedBalanceResp;
import com.khomishchak.cryptoportfolio.model.response.SyncDataResp;

import java.util.List;


public interface ExchangerService {

    FirstlyGeneratedBalanceResp addGeneralExchangerInfo(RegisterExchangerInfoReq exchangerInfoReq, long userId);

    Balance getMainBalance(long userId, ExchangerCode code);

    List<Balance> getAllMainBalances(long userId);

    List<DepositWithdrawalTransaction> getWithdrawalDepositWalletHistory(long userId, ExchangerCode exchangerCode);

    SyncDataResp synchronizeBalanceDataForUser(long userId);

    DeleteExchangerResp deleteExchangerForUser(long userId, ExchangerCode code);
}

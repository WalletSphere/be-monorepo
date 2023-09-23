package com.khomishchak.cryproportfolio.services.exchangers;

import com.khomishchak.cryproportfolio.model.DepositWithdrawalTransaction;
import com.khomishchak.cryproportfolio.model.User;
import com.khomishchak.cryproportfolio.model.enums.ExchangerCode;
import com.khomishchak.cryproportfolio.model.exchanger.Balance;

import java.util.List;


public interface ExchangerService {

    User persistExchangerBalanceForUser(String publicKey, String privateKey, long accoId, ExchangerCode code);

    Balance getMainBalance(long userId, ExchangerCode code);

    List<Balance> getAllMainBalances(long userId);

    List<DepositWithdrawalTransaction> getWithdrawalDepositWalletHistory(long accoId, ExchangerCode exchangerCode);
}

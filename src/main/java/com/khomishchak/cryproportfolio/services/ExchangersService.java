package com.khomishchak.cryproportfolio.services;

import com.khomishchak.cryproportfolio.model.User;
import com.khomishchak.cryproportfolio.model.enums.ExchangerCode;
import com.khomishchak.cryproportfolio.model.exchanger.Balance;

import java.util.List;


public interface ExchangersService {

    User persistExchangerBalanceForUser(String publicKey, String privateKey, long accoId, ExchangerCode code);

    Balance getMainBalance(long userId, ExchangerCode code);

    List<Balance> getAllMainBalances(long userId);
}

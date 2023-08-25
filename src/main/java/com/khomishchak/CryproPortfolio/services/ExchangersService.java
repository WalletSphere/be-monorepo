package com.khomishchak.CryproPortfolio.services;

import com.khomishchak.CryproPortfolio.model.User;
import com.khomishchak.CryproPortfolio.model.enums.ExchangerCode;
import com.khomishchak.CryproPortfolio.model.exchanger.Balance;

import java.util.List;


public interface ExchangersService {

    User persistExchangerBalanceForUser(String publicKey, String privateKey, long accoId, ExchangerCode code);

    Balance getMainBalance(long userId, ExchangerCode code);

    List<Balance> getAllMainBalances(long userId);
}

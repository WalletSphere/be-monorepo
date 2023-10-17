package com.khomishchak.cryptoportfolio.services.exchangers;

import com.khomishchak.cryptoportfolio.model.DepositWithdrawalTransaction;
import com.khomishchak.cryptoportfolio.model.exchanger.Balance;

import java.util.List;

public interface ExchangerConnectorService {

    Balance getMainBalance(long userId);

    List<DepositWithdrawalTransaction> getDepositWithdrawalHistory(long userId);
}

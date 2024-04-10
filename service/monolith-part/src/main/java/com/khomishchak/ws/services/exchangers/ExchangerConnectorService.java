package com.khomishchak.ws.services.exchangers;

import com.khomishchak.ws.model.exchanger.Balance;
import com.khomishchak.ws.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;

public interface ExchangerConnectorService {

    Balance getMainBalance(long userId);

    ExchangerDepositWithdrawalTransactions getDepositWithdrawalHistory(long userId);
}

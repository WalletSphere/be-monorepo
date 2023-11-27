package com.khomishchak.ws.services.exchangers;

import com.khomishchak.ws.model.exchanger.Balance;
import com.khomishchak.ws.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;

import java.util.List;

public interface ExchangerConnectorService {

    Balance getMainBalance(long userId);

    ExchangerDepositWithdrawalTransactions getDepositWithdrawalHistory(long userId);
}

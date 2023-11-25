package com.khomishchak.ws.services.exchangers;

import com.khomishchak.ws.model.DepositWithdrawalTransaction;
import com.khomishchak.ws.model.exchanger.Balance;

import java.util.List;

public interface ExchangerConnectorService {

    Balance getMainBalance(long userId);

    List<DepositWithdrawalTransaction> getDepositWithdrawalHistory(long userId);
}

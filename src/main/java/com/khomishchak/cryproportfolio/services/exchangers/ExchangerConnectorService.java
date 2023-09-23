package com.khomishchak.cryproportfolio.services.exchangers;

import com.khomishchak.cryproportfolio.model.DepositWithdrawalTransaction;
import com.khomishchak.cryproportfolio.model.exchanger.Balance;

import java.util.List;

public interface ExchangerConnectorService {

    Balance getMainBalance(long accoId);

    List<DepositWithdrawalTransaction> getDepositWithdrawalHistory(long accoId);
}

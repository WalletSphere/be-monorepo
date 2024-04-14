package com.walletsphere.wsmonolith.services.exchangers;

import com.walletsphere.wsmonolith.model.exchanger.Balance;
import com.walletsphere.wsmonolith.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;

public interface ExchangerConnectorService {

    Balance getMainBalance(long userId);

    ExchangerDepositWithdrawalTransactions getDepositWithdrawalHistory(long userId);
}

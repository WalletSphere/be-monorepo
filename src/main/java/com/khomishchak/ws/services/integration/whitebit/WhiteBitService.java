package com.khomishchak.ws.services.integration.whitebit;

import com.khomishchak.ws.model.DepositWithdrawalTransaction;
import com.khomishchak.ws.model.exchanger.Balance;

import java.util.List;

public interface WhiteBitService {

    Balance getAccountBalance(long userId);

    List<DepositWithdrawalTransaction> getDepositWithdrawalHistory(long userId);
}

package com.khomishchak.cryproportfolio.services.integration.whitebit;

import com.khomishchak.cryproportfolio.model.DepositWithdrawalTransaction;
import com.khomishchak.cryproportfolio.model.exchanger.Balance;

import java.util.List;

public interface WhiteBitService {

    Balance getAccountBalance(long accoId);

    List<DepositWithdrawalTransaction> getDepositWithdrawalHistory(long accoId);
}

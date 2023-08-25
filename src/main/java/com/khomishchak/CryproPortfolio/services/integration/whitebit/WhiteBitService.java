package com.khomishchak.CryproPortfolio.services.integration.whitebit;

import com.khomishchak.CryproPortfolio.model.exchanger.Balance;

public interface WhiteBitService {

    Balance getAccountBalance(long accoId);
}

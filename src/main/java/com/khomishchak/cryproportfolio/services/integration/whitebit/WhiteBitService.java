package com.khomishchak.cryproportfolio.services.integration.whitebit;

import com.khomishchak.cryproportfolio.model.exchanger.Balance;

public interface WhiteBitService {

    Balance getAccountBalance(long accoId);
}

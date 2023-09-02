package com.khomishchak.cryproportfolio.services.exchangers;

import com.khomishchak.cryproportfolio.model.exchanger.Balance;

public interface ExchangerConnectorService {

    Balance getMainBalance(long accoId);
}

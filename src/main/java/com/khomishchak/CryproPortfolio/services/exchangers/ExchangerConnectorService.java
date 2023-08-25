package com.khomishchak.CryproPortfolio.services.exchangers;

import com.khomishchak.CryproPortfolio.model.exchanger.Balance;

import java.util.List;

public interface ExchangerConnectorService {

    Balance getMainBalance(long accoId);
}

package com.khomishchak.CryproPortfolio.services.exchangers;

import com.khomishchak.CryproPortfolio.model.enums.ExchangerCode;

public interface ExchangerConnectorServiceFactory {

    ExchangerCode getExchangerCode();

    ExchangerConnectorService newInstance();
}
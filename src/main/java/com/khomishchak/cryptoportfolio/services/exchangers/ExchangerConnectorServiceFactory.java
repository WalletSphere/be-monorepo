package com.khomishchak.cryptoportfolio.services.exchangers;

import com.khomishchak.cryptoportfolio.model.enums.ExchangerCode;

public interface ExchangerConnectorServiceFactory {

    ExchangerCode getExchangerCode();

    ExchangerConnectorService newInstance();
}
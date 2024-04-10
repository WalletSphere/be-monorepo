package com.khomishchak.ws.services.exchangers;

import com.khomishchak.ws.model.enums.ExchangerCode;

public interface ExchangerConnectorServiceFactory {

    ExchangerCode getExchangerCode();

    ExchangerConnectorService newInstance();
}
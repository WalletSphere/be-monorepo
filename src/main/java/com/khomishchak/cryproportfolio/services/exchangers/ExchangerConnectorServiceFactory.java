package com.khomishchak.cryproportfolio.services.exchangers;

import com.khomishchak.cryproportfolio.model.enums.ExchangerCode;

public interface ExchangerConnectorServiceFactory {

    ExchangerCode getExchangerCode();

    ExchangerConnectorService newInstance();
}
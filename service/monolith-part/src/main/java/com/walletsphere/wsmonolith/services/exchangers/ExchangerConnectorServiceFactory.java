package com.walletsphere.wsmonolith.services.exchangers;

import com.walletsphere.wsmonolith.model.enums.ExchangerCode;

public interface ExchangerConnectorServiceFactory {

    ExchangerCode getExchangerCode();

    ExchangerConnectorService newInstance();
}
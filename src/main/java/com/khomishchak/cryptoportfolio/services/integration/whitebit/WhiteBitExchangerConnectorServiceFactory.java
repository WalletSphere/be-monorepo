package com.khomishchak.cryptoportfolio.services.integration.whitebit;

import com.khomishchak.cryptoportfolio.model.enums.ExchangerCode;
import com.khomishchak.cryptoportfolio.services.exchangers.ExchangerConnectorService;
import com.khomishchak.cryptoportfolio.services.exchangers.ExchangerConnectorServiceFactory;

import org.springframework.stereotype.Component;

@Component
public class WhiteBitExchangerConnectorServiceFactory implements ExchangerConnectorServiceFactory {

    private final ExchangerCode WHITE_BIT_CODE = ExchangerCode.WHITE_BIT;

    private final WhiteBitService whiteBitService;

    public WhiteBitExchangerConnectorServiceFactory(WhiteBitService whiteBitService) {
        this.whiteBitService = whiteBitService;
    }

    @Override
    public ExchangerCode getExchangerCode() {
        return WHITE_BIT_CODE;
    }

    @Override
    public ExchangerConnectorService newInstance() {
        return new WhiteBitExchangerConnectorService(whiteBitService);
    }
}
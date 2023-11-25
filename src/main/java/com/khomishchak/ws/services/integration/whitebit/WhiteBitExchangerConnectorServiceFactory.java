package com.khomishchak.ws.services.integration.whitebit;

import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.services.exchangers.ExchangerConnectorService;
import com.khomishchak.ws.services.exchangers.ExchangerConnectorServiceFactory;
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
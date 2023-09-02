package com.khomishchak.cryproportfolio.services.integration.whitebit;

import com.khomishchak.cryproportfolio.model.exchanger.Balance;
import com.khomishchak.cryproportfolio.services.exchangers.ExchangerConnectorService;

import org.springframework.stereotype.Service;

@Service
public class WhiteBitExchangerConnectorService implements ExchangerConnectorService {

    private final WhiteBitService whiteBitService;

    public WhiteBitExchangerConnectorService(WhiteBitService whiteBitService) {
        this.whiteBitService = whiteBitService;
    }

    @Override
    public Balance getMainBalance(long accoId) {
        return whiteBitService.getAccountBalance(accoId);
    }
}

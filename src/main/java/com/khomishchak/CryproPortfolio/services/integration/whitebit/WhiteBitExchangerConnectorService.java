package com.khomishchak.CryproPortfolio.services.integration.whitebit;

import com.khomishchak.CryproPortfolio.model.exchanger.Balance;
import com.khomishchak.CryproPortfolio.services.exchangers.ExchangerConnectorService;

import org.springframework.stereotype.Service;

import java.util.List;

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

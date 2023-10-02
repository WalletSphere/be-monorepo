package com.khomishchak.cryptoportfolio.services.integration.whitebit;

import com.khomishchak.cryptoportfolio.model.DepositWithdrawalTransaction;
import com.khomishchak.cryptoportfolio.model.exchanger.Balance;
import com.khomishchak.cryptoportfolio.services.exchangers.ExchangerConnectorService;

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

    @Override
    public List<DepositWithdrawalTransaction> getDepositWithdrawalHistory(long accoId) {
        return whiteBitService.getDepositWithdrawalHistory(accoId);
    }
}

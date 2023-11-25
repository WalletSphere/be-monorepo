package com.khomishchak.ws.services.integration.whitebit;

import com.khomishchak.ws.model.DepositWithdrawalTransaction;
import com.khomishchak.ws.model.exchanger.Balance;
import com.khomishchak.ws.services.exchangers.ExchangerConnectorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WhiteBitExchangerConnectorService implements ExchangerConnectorService {

    private final WhiteBitService whiteBitService;

    public WhiteBitExchangerConnectorService(WhiteBitService whiteBitService) {
        this.whiteBitService = whiteBitService;
    }

    @Override
    public Balance getMainBalance(long userId) {
        return whiteBitService.getAccountBalance(userId);
    }

    @Override
    public List<DepositWithdrawalTransaction> getDepositWithdrawalHistory(long userId) {
        return whiteBitService.getDepositWithdrawalHistory(userId);
    }
}

package com.khomishchak.cryproportfolio.services.integration.whitebit;

import com.khomishchak.cryproportfolio.model.DepositWithdrawalTransaction;
import com.khomishchak.cryproportfolio.model.Transaction;
import com.khomishchak.cryproportfolio.model.exchanger.Balance;
import com.khomishchak.cryproportfolio.services.exchangers.ExchangerConnectorService;

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

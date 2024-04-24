package com.walletsphere.wsmonolith.services.integration.whitebit;

import com.walletsphere.wsmonolith.model.enums.ExchangerCode;
import com.walletsphere.wsmonolith.repositories.BalanceRepository;
import com.walletsphere.wsmonolith.repositories.DepositWithdrawalTransactionsHistoryRepository;
import com.walletsphere.wsmonolith.services.exchangers.ExchangerConnectorService;
import com.walletsphere.wsmonolith.services.exchangers.ExchangerConnectorServiceFactory;
import com.walletsphere.wsmonolith.services.integration.whitebit.mappers.WhiteBitResponseMapper;
import org.springframework.stereotype.Component;

@Component
public class WhiteBitExchangerConnectorServiceFactory implements ExchangerConnectorServiceFactory {

    private final ExchangerCode WHITE_BIT_CODE = ExchangerCode.WHITE_BIT;

    private final BalanceRepository balanceRepository;
    private final DepositWithdrawalTransactionsHistoryRepository depositWithdrawalTransactionsHistoryRepository;
    private final WhiteBitResponseMapper responseMapper;

    private final WhiteBitService whiteBitService;

    public WhiteBitExchangerConnectorServiceFactory(WhiteBitService whiteBitService, BalanceRepository balanceRepository,
                                                    DepositWithdrawalTransactionsHistoryRepository depositWithdrawalTransactionsHistoryRepository,
                                                    WhiteBitResponseMapper responseMapper) {
        this.whiteBitService = whiteBitService;
        this.responseMapper = responseMapper;
        this.balanceRepository = balanceRepository;
        this.depositWithdrawalTransactionsHistoryRepository = depositWithdrawalTransactionsHistoryRepository;
    }

    @Override
    public ExchangerCode getExchangerCode() {
        return WHITE_BIT_CODE;
    }

    @Override
    public ExchangerConnectorService newInstance() {
        return new WhiteBitExchangerConnectorService(whiteBitService, responseMapper, balanceRepository,
                depositWithdrawalTransactionsHistoryRepository);
    }
}
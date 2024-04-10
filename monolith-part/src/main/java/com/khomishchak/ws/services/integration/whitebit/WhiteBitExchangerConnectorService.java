package com.khomishchak.ws.services.integration.whitebit;

import com.khomishchak.ws.exceptions.BalanceNotFoundException;
import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.model.exchanger.Balance;
import com.khomishchak.ws.model.exchanger.Currency;
import com.khomishchak.ws.model.exchanger.transaction.DepositWithdrawalTransaction;
import com.khomishchak.ws.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import com.khomishchak.ws.repositories.BalanceRepository;
import com.khomishchak.ws.repositories.DepositWithdrawalTransactionsHistoryRepository;
import com.khomishchak.ws.services.exchangers.ExchangerConnectorService;
import com.khomishchak.ws.services.integration.whitebit.mappers.WhiteBitResponseMapper;
import com.khomishchak.ws.services.integration.whitebit.model.WhiteBitBalanceResp;
import com.khomishchak.ws.services.integration.whitebit.model.WhiteBitDepositWithdrawalHistoryResp;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WhiteBitExchangerConnectorService implements ExchangerConnectorService {

    private static final ExchangerCode CODE = ExchangerCode.WHITE_BIT;

    private final BalanceRepository balanceRepository;

    private final DepositWithdrawalTransactionsHistoryRepository depositWithdrawalTransactionsHistoryRepository;
    private final WhiteBitResponseMapper responseMapper;

    private final WhiteBitService whiteBitService;

    public WhiteBitExchangerConnectorService(WhiteBitService whiteBitService, WhiteBitResponseMapper responseMapper,
                                             BalanceRepository balanceRepository,
                                             DepositWithdrawalTransactionsHistoryRepository depositWithdrawalTransactionsHistoryRepository) {
        this.whiteBitService = whiteBitService;
        this.responseMapper = responseMapper;
        this.balanceRepository = balanceRepository;
        this.depositWithdrawalTransactionsHistoryRepository = depositWithdrawalTransactionsHistoryRepository;
    }

    @Override
    public Balance getMainBalance(long userId) {
        return whiteBitService.getAccountBalance(userId)
                .map(balanceResp -> handleSuccessfulBalanceResp(userId, balanceResp))
                .block();
    }

    @Override
    public ExchangerDepositWithdrawalTransactions getDepositWithdrawalHistory(long userId) {
        WhiteBitDepositWithdrawalHistoryResp transactionResp = whiteBitService.getDepositWithdrawalHistory(userId).block();
        return handleSuccessfulDepositWithdrawalTransactionsResp(userId, transactionResp);
    }

    private Balance handleSuccessfulBalanceResp(long userId, WhiteBitBalanceResp response) {
        List<Currency> availableCurrencies = responseMapper.mapToCurrencies(response);
        return getPopulatedWithCurrenciesBalance(userId, availableCurrencies);
    }

    private Balance getPopulatedWithCurrenciesBalance(long userId, List<Currency> availableCurrencies) {
        Balance balance = getBalanceByUserIdOrThrowException(userId);
        balance.setCurrencies(availableCurrencies);
        balance.setLastTimeWasUpdated(LocalDateTime.now());
        return balance;
    }

    private ExchangerDepositWithdrawalTransactions handleSuccessfulDepositWithdrawalTransactionsResp(long userId,
                                                                    WhiteBitDepositWithdrawalHistoryResp response) {
        List<DepositWithdrawalTransaction> transactions = responseMapper.mapWithdrawalDepositHistoryToTransactions(response);
        Balance balance = getBalanceByUserIdOrThrowException(userId);
        return getExchangerDepositWithdrawalTransactions(balance, transactions);
    }

    private ExchangerDepositWithdrawalTransactions getExchangerDepositWithdrawalTransactions(Balance balance,
                                                                    List<DepositWithdrawalTransaction> transactions) {
        ExchangerDepositWithdrawalTransactions exchangerTransactions = getExchangerDepositWithdrawalTransactions(balance);
        exchangerTransactions.assigneeTransactionsToExchangerTransactionsEntity(transactions);
        return exchangerTransactions;
    }

    private ExchangerDepositWithdrawalTransactions getExchangerDepositWithdrawalTransactions (Balance balance) {
        return depositWithdrawalTransactionsHistoryRepository.findByBalance_Id(balance.getId())
                .orElseGet(() -> new ExchangerDepositWithdrawalTransactions(balance, CODE));
    }

    private Balance getBalanceByUserIdOrThrowException(long userId) {
        return balanceRepository.findByCodeAndUser_Id(CODE, userId)
                .orElseThrow(() -> new BalanceNotFoundException(String.format("Balance with code:%s for userId:%d was not found", CODE, userId)));
    }
}

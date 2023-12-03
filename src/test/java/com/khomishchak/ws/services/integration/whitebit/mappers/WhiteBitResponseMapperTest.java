package com.khomishchak.ws.services.integration.whitebit.mappers;

import com.khomishchak.ws.model.TransferTransactionType;
import com.khomishchak.ws.model.exchanger.Currency;
import com.khomishchak.ws.model.exchanger.transaction.DepositWithdrawalTransaction;
import com.khomishchak.ws.services.integration.whitebit.model.WhiteBitBalanceResp;
import com.khomishchak.ws.services.integration.whitebit.model.WhiteBitDepositWithdrawalHistoryResp;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class WhiteBitResponseMapperTest {

    private WhiteBitResponseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new WhiteBitResponseMapper();
    }

    @Test
    void shouldMapWhiteBitBalanceRespToInternalCurrenciesList() {
        // given
        Currency btc = new Currency("BTC", 6.13);
        Currency eth = new Currency("ETH", 15.38);
        Currency xrp = new Currency("XRP", 15453.4);

        Map<String, String> BTCPair = Map.of("main_balance", "6.13");
        Map<String, String> ETHPair = Map.of("main_balance", "15.38");
        Map<String, Map<String, String>> currencies = Map.of("BTC", BTCPair, "ETH", ETHPair);

        WhiteBitBalanceResp resp = generateWhiteBitBalanceResp(currencies);

        // when
        List<Currency> result = mapper.mapToCurrencies(resp);

        // then
        assertThat(result.size()).isEqualTo(2);

        assertAll("Currencies validation",
                () -> assertThat(result.contains(btc)).isTrue(),
                () -> assertThat(result.contains(eth)).isTrue(),
                () -> assertThat(result.contains(xrp)).isFalse());

    }

    @Test
    void shouldMapWhiteBitTransactionsRespToInternalTransactionsList() {
        // given
        DepositWithdrawalTransaction transaction = DepositWithdrawalTransaction.depositWithdrawalTransactionBuilder()
                .transactionId("transactionId123")
                .transactionHash("transactionHash123")
                .amount(BigDecimal.valueOf(100.0))
                .ticker("BTC")
                .transferTransactionType(TransferTransactionType.DEPOSIT)
                .build();

        WhiteBitDepositWithdrawalHistoryResp resp = generateWhiteBitDepositWithdrawalHistoryResp();

        // when
        List<DepositWithdrawalTransaction> result = mapper.mapWithdrawalDepositHistoryToTransactions(resp);

        // then
        assertThat(result.size()).isEqualTo(1);

        assertThat(result.contains(transaction)).isTrue();

    }

    private WhiteBitBalanceResp generateWhiteBitBalanceResp(Map<String, Map<String, String>> currencies) {
        WhiteBitBalanceResp resp = new WhiteBitBalanceResp();
        for(Map.Entry<String, Map<String, String>> entry: currencies.entrySet()) {
            resp.setCurrencies(entry.getKey(), entry.getValue());
        }
        return resp;
    }

    private WhiteBitDepositWithdrawalHistoryResp generateWhiteBitDepositWithdrawalHistoryResp() {
        List<WhiteBitDepositWithdrawalHistoryResp.Record> records = new ArrayList<>();

        records.add(generateWhiteBitDepositWithdrawalHistoryRespRecord());

        return new WhiteBitDepositWithdrawalHistoryResp(records);
    }

    private WhiteBitDepositWithdrawalHistoryResp.Record generateWhiteBitDepositWithdrawalHistoryRespRecord() {
        return new WhiteBitDepositWithdrawalHistoryResp.Record(
                "someAddress",
                System.currentTimeMillis(),
                "BTC",
                1,
                100.0,
                0,
                "transactionId123",
                "transactionHash123"
        );
    }
}
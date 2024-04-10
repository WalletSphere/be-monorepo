package com.khomishchak.ws.services.integration.whitebit.mappers;

import com.khomishchak.ws.model.TransferTransactionType;
import com.khomishchak.ws.model.exchanger.Currency;
import com.khomishchak.ws.model.exchanger.transaction.DepositWithdrawalTransaction;
import com.khomishchak.ws.model.exchanger.transaction.TransactionStatus;
import com.khomishchak.ws.services.integration.whitebit.model.WhiteBItTransactionStatusCode;
import com.khomishchak.ws.services.integration.whitebit.model.WhiteBitBalanceResp;
import com.khomishchak.ws.services.integration.whitebit.model.WhiteBitDepositWithdrawalHistoryResp;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class WhiteBitResponseMapper {

    public List<Currency> mapToCurrencies (WhiteBitBalanceResp resp) {
        List<Currency> result  = new ArrayList<>();

        for(Map.Entry<String, String> unit: resp.getCurrencies().entrySet()) {
            if(unit.getValue().equals("0")) {
                continue;
            }
            Currency currency = new Currency();
            currency.setCurrencyCode(unit.getKey());
            currency.setAmount(Double.parseDouble(unit.getValue()));

            result.add(currency);
        }

        return result;
    }

    public List<DepositWithdrawalTransaction> mapWithdrawalDepositHistoryToTransactions(WhiteBitDepositWithdrawalHistoryResp resp) {
        List<DepositWithdrawalTransaction> result = new ArrayList<>();
        resp.getRecords()
                .forEach(record -> {
                    DepositWithdrawalTransaction transaction = DepositWithdrawalTransaction.depositWithdrawalTransactionBuilder()
                            .transactionId(record.getTransactionId())
                            .transactionHash(record.getTransactionHash())
                            .createdAt(LocalDateTime.ofInstant(Instant.ofEpochMilli(record.getCreatedAt() * 1000), ZoneId.systemDefault()))
                            .amount(BigDecimal.valueOf(record.getAmount()))
                            .ticker(record.getTicker())
                            .transferTransactionType(record.getMethod() == 1 ? TransferTransactionType.DEPOSIT : TransferTransactionType.WITHDRAWAL)
                            .transactionStatus(getTransactionsStatus(record.getStatus()))
                            .build();

                    result.add(transaction);
                });

        return result;
    }

    private TransactionStatus getTransactionsStatus(int wbtTransactionCode) {
        WhiteBItTransactionStatusCode transactionsStatus = WhiteBItTransactionStatusCode.getStatusByCode(wbtTransactionCode);
        return switch (transactionsStatus) {
            // more will be added once new transactions logic will be added
            case COMPLETED -> TransactionStatus.COMPLETED;
            default -> null;
        };
    }
}

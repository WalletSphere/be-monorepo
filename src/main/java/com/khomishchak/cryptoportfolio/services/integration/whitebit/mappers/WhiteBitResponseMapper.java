package com.khomishchak.cryptoportfolio.services.integration.whitebit.mappers;

import com.khomishchak.cryptoportfolio.model.DepositWithdrawalTransaction;
import com.khomishchak.cryptoportfolio.model.TransactionType;
import com.khomishchak.cryptoportfolio.model.exchanger.Currency;
import com.khomishchak.cryptoportfolio.services.integration.whitebit.model.WhiteBitBalanceResp;
import com.khomishchak.cryptoportfolio.services.integration.whitebit.model.WhiteBitDepositWithdrawalHistoryResp;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
                            .createdAt(new Date(TimeUnit.SECONDS.toMillis(record.getCreatedAt())))
                            .amount(BigDecimal.valueOf(record.getAmount()))
                            .ticker(record.getTicker())
                            .transactionType(record.getMethod() == 1 ? TransactionType.DEPOSIT : TransactionType.WITHDRAWAL)
                            .build();

                    result.add(transaction);
                });

        return result;
    }
}

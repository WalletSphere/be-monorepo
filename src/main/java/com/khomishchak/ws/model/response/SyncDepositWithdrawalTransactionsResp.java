package com.khomishchak.ws.model.response;

import com.khomishchak.ws.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;

import java.util.List;

public record SyncDepositWithdrawalTransactionsResp(List<ExchangerDepositWithdrawalTransactions> transactions) {
}

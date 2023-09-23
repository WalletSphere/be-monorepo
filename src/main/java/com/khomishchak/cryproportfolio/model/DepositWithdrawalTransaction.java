package com.khomishchak.cryproportfolio.model;


import java.util.Date;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DepositWithdrawalTransaction extends Transaction {

    private TransactionType transactionType;

    @Builder(builderMethodName = "depositWithdrawalTransactionBuilder")
    public DepositWithdrawalTransaction(String transactionId, String transactionHash, String ticker, double fee, double amount, Date createdAt, TransactionType transactionType) {
        super(transactionId, transactionHash, ticker, fee, amount, createdAt);
        this.transactionType = transactionType;
    }
}

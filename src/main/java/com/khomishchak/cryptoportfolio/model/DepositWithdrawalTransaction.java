package com.khomishchak.cryptoportfolio.model;


import java.math.BigDecimal;
import java.util.Date;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DepositWithdrawalTransaction extends Transaction {

    private TransactionType transactionType;

    @Builder(builderMethodName = "depositWithdrawalTransactionBuilder")
    public DepositWithdrawalTransaction(String transactionId, String transactionHash, String ticker, BigDecimal fee, BigDecimal amount, Date createdAt, TransactionType transactionType) {
        super(transactionId, transactionHash, ticker, fee, amount, createdAt);
        this.transactionType = transactionType;
    }
}

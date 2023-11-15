package com.khomishchak.cryptoportfolio.model;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DepositWithdrawalTransaction extends Transaction {

    private TransferTransactionType transferTransactionType;

    @Builder(builderMethodName = "depositWithdrawalTransactionBuilder")
    public DepositWithdrawalTransaction(String transactionId, String transactionHash, String ticker, BigDecimal fee, BigDecimal amount, LocalDateTime createdAt, TransferTransactionType transferTransactionType) {
        super(transactionId, transactionHash, ticker, fee, amount, createdAt);
        this.transferTransactionType = transferTransactionType;
    }
}

package com.khomishchak.ws.model;


import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class DepositWithdrawalTransaction extends Transaction {

    private TransferTransactionType transferTransactionType;

    @Builder(builderMethodName = "depositWithdrawalTransactionBuilder")
    public DepositWithdrawalTransaction(String transactionId, String transactionHash, String ticker, BigDecimal fee, BigDecimal amount, LocalDateTime createdAt, TransferTransactionType transferTransactionType) {
        super(transactionId, transactionHash, ticker, fee, amount, createdAt);
        this.transferTransactionType = transferTransactionType;
    }
}

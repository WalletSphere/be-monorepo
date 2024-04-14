package com.walletsphere.wsmonolith.model.filter;

import com.walletsphere.wsmonolith.model.TransferTransactionType;
import com.walletsphere.wsmonolith.model.exchanger.transaction.TransactionStatus;

import java.time.LocalDateTime;

public record TransactionSearchCriteria(String ticker, TransferTransactionType transferTransactionType,
                                        LocalDateTime fromDate, LocalDateTime toDate, TransactionStatus transactionStatus){}

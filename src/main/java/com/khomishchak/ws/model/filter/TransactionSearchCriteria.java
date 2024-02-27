package com.khomishchak.ws.model.filter;

import com.khomishchak.ws.model.TransferTransactionType;
import com.khomishchak.ws.model.exchanger.transaction.TransactionStatus;

import java.time.LocalDateTime;

public record TransactionSearchCriteria(String ticker, TransferTransactionType transferTransactionType,
                                        LocalDateTime fromDate, LocalDateTime toDate, TransactionStatus transactionStatus){}

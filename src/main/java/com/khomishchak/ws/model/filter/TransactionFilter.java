package com.khomishchak.ws.model.filter;

import com.khomishchak.ws.model.TransferTransactionType;
import com.khomishchak.ws.model.exchanger.transaction.DepositWithdrawalTransaction;
import com.khomishchak.ws.model.exchanger.transaction.Transaction;
import com.khomishchak.ws.model.exchanger.transaction.TransactionStatus;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class TransactionFilter {

    private static Predicate<Transaction> isTickerEqual(String ticker) {
        return transaction -> transaction.getTicker().equalsIgnoreCase(ticker);
    }

    private static Predicate<Transaction> isTransferTypeEqual(TransferTransactionType transferTransactionType) {
        return transaction -> {
            if (transaction instanceof DepositWithdrawalTransaction) {
                return ((DepositWithdrawalTransaction) transaction).getTransferTransactionType().equals(transferTransactionType);
            }
            return false;
        };
    }

    private static Predicate<Transaction> isFrom(LocalDateTime time) {
        return transaction -> (time == null || transaction.getCreatedAt().isAfter(time));
    }

    private static Predicate<Transaction> isUntil(LocalDateTime time) {
        return transaction -> (time == null || transaction.getCreatedAt().isBefore(time));
    }

    private static Predicate<Transaction> isTransactionsStatusEqual(TransactionStatus transactionStatus) {
        return transaction -> Objects.equals(transactionStatus, transaction.getTransactionStatus());
    }

    public static <T extends Transaction> List<T> filterTransactions(List<T> transactions, TransactionSearchCriteria criteria) {
        Predicate<T> resultPredicate = t -> true;
        if(StringUtils.hasText(criteria.ticker())) {
            resultPredicate = resultPredicate.and(isTickerEqual(criteria.ticker()));
        }
        if(criteria.fromDate() != null) {
            resultPredicate = resultPredicate.and(isFrom(criteria.fromDate()));
        }
        if(criteria.toDate() != null) {
            resultPredicate = resultPredicate.and(isUntil(criteria.toDate()));
        }
        if(criteria.transferTransactionType() != null) {
            resultPredicate = resultPredicate.and(isTransferTypeEqual(criteria.transferTransactionType()));
        }
        if (criteria.transactionStatus() != null) {
            resultPredicate = resultPredicate.and(isTransactionsStatusEqual(criteria.transactionStatus()));
        }

        return transactions.stream()
                .filter(resultPredicate)
                .toList();
    }
}

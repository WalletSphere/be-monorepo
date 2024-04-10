package com.khomishchak.ws.services.goal.impl;

import com.khomishchak.ws.model.TransferTransactionType;
import com.khomishchak.ws.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import com.khomishchak.ws.model.exchanger.transaction.Transaction;
import com.khomishchak.ws.model.goals.CommonGoalType;
import com.khomishchak.ws.model.goals.SelfGoal;
import com.khomishchak.ws.services.exchangers.ExchangerService;
import com.khomishchak.ws.services.goal.SelfGoalValidator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DepositSelfGoalValidator implements SelfGoalValidator {

    private final ExchangerService exchangerService;

    public DepositSelfGoalValidator(ExchangerService exchangerService) {
        this.exchangerService = exchangerService;
    }

    @Override
    public CommonGoalType getCommonGoalType() {
        return CommonGoalType.DEPOSIT_GOAL;
    }

    @Override
    public boolean isAchieved(SelfGoal goal) {
        double depositValue = getDepositValueForPeriod(goal.getUser().getId(), goal.getTicker());

        goal.setCurrentAmount(depositValue);
        return depositValue > goal.getGoalAmount();
    }

    private double getDepositValueForPeriod(long userId, String ticker) {
        return exchangerService.getWithdrawalDepositWalletHistory(userId).stream()
                .map(transactions -> getDepositValueForPeriodForSingleIntegratedBalance(transactions, ticker))
                .reduce(0.0, Double::sum);
    }

    private double getDepositValueForPeriodForSingleIntegratedBalance(ExchangerDepositWithdrawalTransactions transactions,
                                                                      String ticker) {
        return transactions.getTransactions().stream()
                .filter(transaction -> transaction.getTicker().equalsIgnoreCase(ticker) &&
                        transaction.getTransferTransactionType().equals(TransferTransactionType.DEPOSIT))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
    }
}

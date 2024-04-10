package com.khomishchak.ws.services.goal.impl;

import com.khomishchak.ws.model.TransferTransactionType;
import com.khomishchak.ws.model.User;
import com.khomishchak.ws.model.exchanger.transaction.DepositWithdrawalTransaction;
import com.khomishchak.ws.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import com.khomishchak.ws.model.goals.GoalType;
import com.khomishchak.ws.model.goals.SelfGoal;
import com.khomishchak.ws.services.exchangers.ExchangerService;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DepositSelfGoalValidatorTest {

    private static final long USER_ID = 1L;
    private static final long TRANSACTIONS_ID = 4L;

    @Mock
    private ExchangerService exchangerService;

    private DepositSelfGoalValidator validator;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(USER_ID).build();
        validator = new DepositSelfGoalValidator(exchangerService);
    }

    @Test
    void shouldReturnTrueWhenGoalAchieved() {
        // given
        SelfGoal goal = createTestGoal(100.0);
        when(exchangerService.getWithdrawalDepositWalletHistory(goal.getUser().getId()))
                .thenReturn(createMockSingleTransactionList(150.0));

        // when
        boolean result = validator.isAchieved(goal);

        // then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenGoalNotAchieved() {
        // Arrange
        SelfGoal goal = createTestGoal(200.0);
        when(exchangerService.getWithdrawalDepositWalletHistory(goal.getUser().getId()))
                .thenReturn(createMockSingleTransactionList(150.0));

        // Act
        boolean result = validator.isAchieved(goal);

        // Assert
        assertFalse(result);
    }

    private SelfGoal createTestGoal(double goalAmount) {
        return SelfGoal.builder()
                .user(testUser)
                .ticker("BTC")
                .goalType(GoalType.DAILY_DEPOSIT_GOAL)
                .goalAmount(goalAmount)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .build();
    }

    private List<ExchangerDepositWithdrawalTransactions> createMockSingleTransactionList(double depositValue) {
        List<DepositWithdrawalTransaction> transactions = new ArrayList<>();
        transactions.add(
                DepositWithdrawalTransaction.depositWithdrawalTransactionBuilder()
                        .createdAt(LocalDateTime.now())
                        .transactionId("transactionId")
                        .ticker("BTC")
                        .transferTransactionType(TransferTransactionType.DEPOSIT)
                        .amount(BigDecimal.valueOf(depositValue))
                        .build()
        );

        return List.of(
                ExchangerDepositWithdrawalTransactions.builder()
                        .id(TRANSACTIONS_ID)
                        .transactions(transactions)
                        .build()
        );
    }
}
package com.khomishchak.ws.services;

import com.khomishchak.ws.model.TransactionType;
import com.khomishchak.ws.model.TransferTransactionType;
import com.khomishchak.ws.model.User;
import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.model.exchanger.Balance;
import com.khomishchak.ws.model.exchanger.transaction.DepositWithdrawalTransaction;
import com.khomishchak.ws.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import com.khomishchak.ws.model.goals.*;
import com.khomishchak.ws.repositories.CryptoGoalsTableRepository;
import com.khomishchak.ws.repositories.SelfGoalRepository;
import com.khomishchak.ws.services.exchangers.ExchangerService;
import com.khomishchak.ws.services.goal.SelfGoalValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalsServiceImplTest {

    private static final long USER_ID = 1L;
    private static final long CRYPTO_GOALS_TABLE_ID = 1L;

    @Mock
    private CryptoGoalsTableRepository cryptoGoalsTableRepository;
    @Mock
    private UserService userService;
    @Mock
    private SelfGoalRepository selfGoalRepository;
    @Mock
    private ExchangerService exchangerService;
    @Mock
    private SelfGoalValidator selfGoalValidator;

    private GoalsServiceImpl goalsService;
    
    User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(USER_ID);

        when(selfGoalValidator.getCommonGoalType()).thenReturn(CommonGoalType.DEPOSIT_GOAL); // does not matter what is the type

        goalsService = new GoalsServiceImpl(cryptoGoalsTableRepository, userService, selfGoalRepository, exchangerService,
                List.of(selfGoalValidator));
    }

    @Test
    void shouldCreateCryptoGoalsTable() {
        // given
        CryptoGoalsTableRecord record = CryptoGoalsTableRecord.builder()
                .goalQuantity(BigDecimal.TEN)
                .quantity(BigDecimal.ONE)
                .build();

        CryptoGoalsTable cryptoGoalsTableToBeCreated = CryptoGoalsTable.builder()
                .tableRecords(List.of(record))
                .build();

        CryptoGoalsTable cryptoGoalsTable = CryptoGoalsTable.builder()
                .id(CRYPTO_GOALS_TABLE_ID)
                .tableRecords(List.of(record))
                .build();

        testUser = User.builder().id(USER_ID).build();

        when(userService.getUserById(eq(USER_ID))).thenReturn(testUser);
        when(cryptoGoalsTableRepository.save(eq(cryptoGoalsTableToBeCreated))).thenReturn(cryptoGoalsTable);

        // when
        CryptoGoalsTable actualTable = goalsService.createCryptoGoalsTable(USER_ID, cryptoGoalsTableToBeCreated);

        // then
        assertThat(actualTable.getTableRecords()).isNotNull();

        CryptoGoalsTableRecord calculatedRecord =  actualTable.getTableRecords().get(0);
        assertThat(calculatedRecord.getLeftToBuy()).isEqualTo(new BigDecimal(9));
        assertThat(calculatedRecord.getDonePercentage()).isEqualTo(new BigDecimal("10.0"));
        assertThat(calculatedRecord.isFinished()).isEqualTo(false);
    }

    @Test
    void shouldReturnCryptoGoalsTable() {
        // given
        CryptoGoalsTableRecord record = CryptoGoalsTableRecord.builder()
                .goalQuantity(BigDecimal.TEN)
                .quantity(BigDecimal.ONE)
                .build();

        CryptoGoalsTable cryptoGoalsTable = CryptoGoalsTable.builder()
                .id(CRYPTO_GOALS_TABLE_ID)
                .tableRecords(List.of(record))
                .build();
        testUser = User.builder().cryptoGoalsTable(cryptoGoalsTable).build() ;

        when(userService.getUserById(USER_ID)).thenReturn(testUser);

        // when
        CryptoGoalsTable actualTable = goalsService.getCryptoGoalsTable(USER_ID);

        // then
        assertThat(actualTable.getTableRecords()).isNotNull();

        CryptoGoalsTableRecord calculatedRecord =  actualTable.getTableRecords().get(0);
        assertThat(calculatedRecord.getLeftToBuy()).isEqualTo(new BigDecimal(9));
        assertThat(calculatedRecord.getDonePercentage()).isEqualTo(new BigDecimal("10.0"));
        assertThat(calculatedRecord.isFinished()).isEqualTo(false);
    }

    @Test
    void shouldUpdateWholeCryptoGoalsTable() {
        // given
        CryptoGoalsTableRecord newRecord = CryptoGoalsTableRecord.builder()
                .name("BTC")
                .goalQuantity(BigDecimal.TEN)
                .quantity(BigDecimal.TEN)
                .build();

        CryptoGoalsTable newCryptoGoalsTable = CryptoGoalsTable.builder()
                .tableRecords(List.of(newRecord))
                .build();

        when(cryptoGoalsTableRepository.save(eq(newCryptoGoalsTable))).thenReturn(newCryptoGoalsTable);

        // when
        CryptoGoalsTable actualTable = goalsService.updateCryptoGoalsTable(newCryptoGoalsTable);

        // then
        assertThat(actualTable.getTableRecords()).isNotNull();

        CryptoGoalsTableRecord calculatedRecord =  actualTable.getTableRecords().get(0);
        assertThat(calculatedRecord.getLeftToBuy()).isEqualTo(BigDecimal.ZERO);
        assertThat(calculatedRecord.getDonePercentage()).isEqualTo(new BigDecimal("100.0"));
        assertThat(calculatedRecord.isFinished()).isEqualTo(true);
    }

    @Test
    void shouldUpdateCryptoGoalsTableWithSingleTransaction_whenTransactionIsBuy() {
        // given
        CryptoGoalTableTransaction transaction = CryptoGoalTableTransaction.builder()
                .id(1L)
                .transactionType(TransactionType.BUY)
                .averagePrice(BigDecimal.valueOf(10))
                .quantity(BigDecimal.ONE)
                .name("BTC")
                .build();

        CryptoGoalsTableRecord record = CryptoGoalsTableRecord.builder()
                .name("BTC")
                .averageCost(BigDecimal.valueOf(20))
                .goalQuantity(BigDecimal.TEN)
                .quantity(BigDecimal.TEN)
                .build();

        CryptoGoalsTable cryptoGoalsTable = CryptoGoalsTable.builder()
                .tableRecords(List.of(record))
                .build();

        when(cryptoGoalsTableRepository.findById(eq(1L))).thenReturn(Optional.of(cryptoGoalsTable));
        when(cryptoGoalsTableRepository.save(eq(cryptoGoalsTable))).thenReturn(cryptoGoalsTable);

        // when
        CryptoGoalsTable table = goalsService.updateCryptoGoalsTable(transaction, 1L);

        // then
        assertThat(table.getTableRecords()).isNotNull();
        assertThat(table.getTableRecords().size()).isEqualTo(1);

        CryptoGoalsTableRecord calculatedRecord =  table.getTableRecords().get(0);
        assertThat(calculatedRecord.getLeftToBuy()).isEqualTo(BigDecimal.ZERO);
        assertThat(calculatedRecord.getDonePercentage()).isEqualTo(new BigDecimal("110.0"));
        assertThat(calculatedRecord.getQuantity()).isEqualTo(new BigDecimal("11"));
        assertThat(calculatedRecord.getAverageCost()).isEqualTo(new BigDecimal("19.0909"));
        assertThat(calculatedRecord.isFinished()).isEqualTo(true);
    }

    @Test
    void shouldUpdateCryptoGoalsTableWithSingleTransaction_whenTransactionIsSell() {
        // given
        CryptoGoalTableTransaction transaction = CryptoGoalTableTransaction.builder()
                .id(1L)
                .transactionType(TransactionType.SELL)
                .averagePrice(BigDecimal.valueOf(10))
                .quantity(BigDecimal.ONE)
                .name("BTC")
                .build();

        CryptoGoalsTableRecord record = CryptoGoalsTableRecord.builder()
                .name("BTC")
                .averageCost(BigDecimal.valueOf(20))
                .goalQuantity(BigDecimal.TEN)
                .quantity(BigDecimal.TEN)
                .finished(true)
                .build();

        CryptoGoalsTable cryptoGoalsTable = CryptoGoalsTable.builder()
                .tableRecords(List.of(record))
                .build();

        when(cryptoGoalsTableRepository.findById(eq(1L))).thenReturn(Optional.of(cryptoGoalsTable));
        when(cryptoGoalsTableRepository.save(eq(cryptoGoalsTable))).thenReturn(cryptoGoalsTable);

        // when
        CryptoGoalsTable table = goalsService.updateCryptoGoalsTable(transaction, 1L);

        // then
        assertThat(table.getTableRecords()).isNotNull();
        assertThat(table.getTableRecords().size()).isEqualTo(1);

        CryptoGoalsTableRecord calculatedRecord =  table.getTableRecords().get(0);
        assertThat(calculatedRecord.getLeftToBuy()).isEqualTo(BigDecimal.ONE);
        assertThat(calculatedRecord.getDonePercentage()).isEqualTo(new BigDecimal("90.0"));
        assertThat(calculatedRecord.getQuantity()).isEqualTo(new BigDecimal("9"));
        assertThat(calculatedRecord.getAverageCost()).isEqualTo(new BigDecimal("21.1111"));
        assertThat(calculatedRecord.isFinished()).isEqualTo(false);
    }

    @Test
    void shouldReturnSelfGoalsStatus_whenUserDontHaveBalanceIntegration() {
        // given
        GoalType goalType = GoalType.DAILY_DEPOSIT_GOAL;

        SelfGoal selfGoal = SelfGoal.builder()
                .goalAmount(10)
                .goalType(goalType)
                .ticker("UAH")
                .startDate(goalType.getStartTime(1))
                .endDate(goalType.getEndTime())
                .isClosed(false)
                .build();

        List<SelfGoal> persistedSelfGoals = List.of(selfGoal);

        when(selfGoalRepository.findAllByUserId(USER_ID)).thenReturn(persistedSelfGoals);
        when(exchangerService.getWithdrawalDepositWalletHistory(USER_ID)).thenReturn(Collections.emptyList());

        // when
        List<SelfGoal> selfGoals = goalsService.getSelfGoals(USER_ID);

        // then
        assertThat(selfGoals.isEmpty()).isFalse();

        SelfGoal givenGoal = selfGoals.get(0);
        assertThat(givenGoal.getCurrentAmount()).isEqualTo(0);
        assertThat(givenGoal.isAchieved()).isFalse();
    }

    @Test
    void shouldReturnSelfGoalsStatus_whenUserHaveBalanceIntegration() {
        // given
        GoalType goalType = GoalType.DAILY_DEPOSIT_GOAL;

        Balance balance = Balance.builder()
                .code(ExchangerCode.WHITE_BIT)
                .build();

        DepositWithdrawalTransaction transaction = DepositWithdrawalTransaction.depositWithdrawalTransactionBuilder()
                .amount(BigDecimal.ONE)
                .ticker("UAH")
                .transferTransactionType(TransferTransactionType.DEPOSIT)
                .createdAt(LocalDateTime.now())
                .build();

        ExchangerDepositWithdrawalTransactions singleExchangerTransactions = ExchangerDepositWithdrawalTransactions.builder()
                .transactions(List.of(transaction))
                .code(ExchangerCode.WHITE_BIT)
                .build();

        testUser.setBalances(List.of(balance));

        SelfGoal selfGoal = SelfGoal.builder()
                .goalAmount(10)
                .goalType(GoalType.DAILY_DEPOSIT_GOAL)
                .ticker("UAH")
                .startDate(goalType.getStartTime(1))
                .endDate(goalType.getEndTime())
                .isClosed(false)
                .build();

        List<SelfGoal> persistedSelfGoals = List.of(selfGoal);

        when(selfGoalRepository.findAllByUserId(USER_ID)).thenReturn(persistedSelfGoals);
        when(exchangerService.getWithdrawalDepositWalletHistory(USER_ID)).thenReturn(List.of(singleExchangerTransactions));

        // when
        List<SelfGoal> selfGoals = goalsService.getSelfGoals(USER_ID);

        // then
        assertThat(selfGoals.isEmpty()).isFalse();

        SelfGoal givenGoal = selfGoals.get(0);
        assertThat(givenGoal.getCurrentAmount()).isEqualTo(1);
        assertThat(givenGoal.isAchieved()).isFalse();
    }

    @Test
    void shouldCreateNewSelfGoal() {
        // given
        GoalType goalType = GoalType.DAILY_DEPOSIT_GOAL;

        SelfGoal selfGoal = SelfGoal.builder()
                .goalAmount(10)
                .goalType(goalType)
                .ticker("UAH")
                .build();

        when(userService.getUserById(USER_ID)).thenReturn(testUser);
        when(userService.saveUser(testUser)).thenReturn(testUser);

        // when
        List<SelfGoal> createdGoals = goalsService.createSelfGoals(USER_ID, List.of(selfGoal));

        // then
        SelfGoal savedGoal = createdGoals.get(0);
        assertThat(savedGoal.getUser()).isEqualTo(testUser);
        assertThat(savedGoal.getStartDate()).isEqualTo(goalType.getStartTime(1));
        assertThat(savedGoal.getEndDate()).isEqualTo(goalType.getEndTime());
        assertThat(savedGoal.isAchieved()).isFalse();
    }

    @Test
    void shouldReturnTrue_whenGoalWasAchieved() {
        // given
        SelfGoal overdueGoal = SelfGoal.builder()
                .goalType(GoalType.DAILY_DEPOSIT_GOAL)
                .build();

        when(selfGoalValidator.isAchieved(overdueGoal)).thenReturn(true);

        // when
        boolean achieved = goalsService.overdueGoalIsAchieved(overdueGoal);

        // then
        assertThat(achieved).isTrue();
    }

    @Test
    void shouldReturnFalse_whenGoalWasNotAchieved() {
        // given
        SelfGoal overdueGoal = SelfGoal.builder()
                .goalType(GoalType.DAILY_DEPOSIT_GOAL)
                .build();

        when(selfGoalValidator.isAchieved(overdueGoal)).thenReturn(false);

        // when
        boolean achieved = goalsService.overdueGoalIsAchieved(overdueGoal);

        // then
        assertThat(achieved).isFalse();
    }
}
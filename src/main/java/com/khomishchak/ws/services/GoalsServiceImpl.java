package com.khomishchak.ws.services;

import com.khomishchak.ws.exceptions.GoalsTableNotFoundException;
import com.khomishchak.ws.model.Transaction;
import com.khomishchak.ws.model.TransactionType;
import com.khomishchak.ws.model.TransferTransactionType;
import com.khomishchak.ws.model.User;
import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.model.enums.GoalType;
import com.khomishchak.ws.model.exchanger.Balance;
import com.khomishchak.ws.model.goals.*;
import com.khomishchak.ws.repositories.CryptoGoalsTableRepository;
import com.khomishchak.ws.repositories.SelfGoalRepository;
import com.khomishchak.ws.services.exchangers.ExchangerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GoalsServiceImpl implements GoalsService {

    public static final int END_OF_PREVIOUS_PERIOD = 2;
    public static final int END_OF_CURRENT_PERIOD = 1;
    public static final int PERCENTAGE_SCALE = 100;
    private final CryptoGoalsTableRepository cryptoGoalsTableRepository;
    private final UserService userService;
    private final SelfGoalRepository selfGoalRepository;
    private final ExchangerService exchangerService;

    public GoalsServiceImpl(CryptoGoalsTableRepository cryptoGoalsTableRepository, UserService userService,
            SelfGoalRepository selfGoalRepository, ExchangerService exchangerService) {
        this.cryptoGoalsTableRepository = cryptoGoalsTableRepository;
        this.userService = userService;
        this.selfGoalRepository = selfGoalRepository;
        this.exchangerService = exchangerService;
    }

    @Override
    public CryptoGoalsTable createCryptoGoalsTable(Long userId, CryptoGoalsTable tableRequest) {
        User user = userService.getUserById(userId);
        user.setCryptoGoalsTable(tableRequest);
        tableRequest.setUser(user);
        return saveCryptoTable(tableRequest);
    }

    @Override
    public CryptoGoalsTable getCryptoGoalsTable(Long userId) {
        CryptoGoalsTable table  = userService.getUserById(userId).getCryptoGoalsTable();
        table.getTableRecords().forEach(this::setPostQuantityValues);
        return table;
    }

    @Override
    public CryptoGoalsTable updateCryptoGoalsTable(CryptoGoalsTable cryptoGoalsTable) {
        CryptoGoalsTable cryptoGoalsTableEn = getCryptoGoalsTable(cryptoGoalsTable.getId());

        cryptoGoalsTable.getTableRecords().forEach(r -> {
            cryptoGoalsTableEn.getTableRecords().forEach(rEn -> {
                if (r.getName().equals(rEn.getName())) {
                    rEn.setGoalQuantity(r.getGoalQuantity());
                    rEn.setQuantity(r.getQuantity());
                    rEn.setAverageCost(r.getAverageCost());
                }
            });
        });

        return saveCryptoTable(cryptoGoalsTable);
    }

    @Override
    public CryptoGoalsTable updateCryptoGoalsTable(CryptoGoalTableTransaction transaction, long tableId) {
        CryptoGoalsTable cryptoGoalsTable = getCryptoGoalsTableOrThrowException(tableId);
        updateCryptoGoalsTableWithSingleTransaction(cryptoGoalsTable, transaction);
        return saveCryptoTable(cryptoGoalsTable);
    }

    private void updateCryptoGoalsTableWithSingleTransaction(CryptoGoalsTable cryptoGoalsTable,
                                                             CryptoGoalTableTransaction transaction) {
        cryptoGoalsTable.getTableRecords().forEach(record -> applyTransactionToRecord(record, transaction));
    }

    private void applyTransactionToRecord(CryptoGoalsTableRecord record, CryptoGoalTableTransaction transaction) {
        if(!record.getName().equals(transaction.getName())) return;
        record.setAverageCost(calculateNewAveragePriceAfterTransaction(record, transaction));
        record.setQuantity(calculateNewQuantity(record, transaction));
    }

    private BigDecimal calculateNewQuantity(CryptoGoalsTableRecord oldRecord, CryptoGoalTableTransaction transaction) {
        return TransactionType.BUY.equals(transaction.getTransactionType())
            ? oldRecord.getQuantity().add(transaction.getQuantity())
            : oldRecord.getQuantity().subtract(transaction.getQuantity());
    }

    private BigDecimal calculateNewAveragePriceAfterTransaction(CryptoGoalsTableRecord oldRecord,
                                                                CryptoGoalTableTransaction transaction) {
        TransactionChangeStateDTO transactionChangeStateDTO = mapToTransactionChangeStateDTO(oldRecord, transaction);
        return calculateAveragePrice(transactionChangeStateDTO);
    }

    private BigDecimal calculateAveragePrice(TransactionChangeStateDTO transactionDTO) {
        BigDecimal oldTotalValue = calculateTotalValue(transactionDTO.oldRecordAveragePrice(), transactionDTO.oldRecordQuantity());
        BigDecimal newOperationTotalValue = calculateTotalValue(transactionDTO.newOperationAveragePrice(), transactionDTO.newOperationQuantity());

        BigDecimal resultTotalPrice = null;
        BigDecimal resultQuantity = null;

        if(TransactionType.BUY.equals(transactionDTO.transactionType())) {
            resultTotalPrice = oldTotalValue.add(newOperationTotalValue);
            resultQuantity = transactionDTO.oldRecordQuantity().add(transactionDTO.newOperationQuantity());
        } else {
            resultTotalPrice = oldTotalValue.subtract(newOperationTotalValue);
            resultQuantity = transactionDTO.oldRecordQuantity().subtract(transactionDTO.newOperationQuantity());
        }

        if (resultQuantity.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        return resultTotalPrice.divide(resultQuantity, 4, RoundingMode.DOWN);
    }

    private BigDecimal calculateTotalValue(BigDecimal quantity, BigDecimal averagePrice) {
        return quantity.multiply(averagePrice);
    }

    private TransactionChangeStateDTO mapToTransactionChangeStateDTO(CryptoGoalsTableRecord record,
                                                                     CryptoGoalTableTransaction transaction) {
        return TransactionChangeStateDTO.builder()
                .newOperationAveragePrice(transaction.getAveragePrice())
                .newOperationQuantity(transaction.getQuantity())
                .oldRecordAveragePrice(record.getAverageCost())
                .oldRecordQuantity(record.getQuantity())
                .transactionType(transaction.getTransactionType())
                .build();
    }

    @Override
    public List<SelfGoal> getSelfGoals(Long userId) {

        List<SelfGoal> result = selfGoalRepository.findAllByUserId(userId);

        result.forEach(goal -> {
            goal.setCurrentAmount(getDepositValueForPeriod(userService.getUserById(userId), goal.getTicker(), goal.getStartDate(), goal.getEndDate()));
            goal.setAchieved(goal.getCurrentAmount() > goal.getGoalAmount());
        });
        return result;
    }

    @Override
    @Transactional
    public List<SelfGoal> createSelfGoals(Long userId, List<SelfGoal> goals) {
        User user = userService.getUserById(userId);
        user.setSelfGoals(goals);

        goals.forEach(g -> {
            g.setUser(user);
            g.setStartDate(LocalDateTime.now());
            g.setEndDate(g.getGoalType().getEndTime());
            g.setAchieved(g.getCurrentAmount() > g.getGoalAmount());
            g.setCurrentAmount(getDepositValueForPeriod(user, g.getTicker(), g.getStartDate(), g.getEndDate()));
        });

        userService.saveUser(user);

        return goals;
    }

    // TODO: should be replaced with strategy pattern to handle multiple goal types, not only deposit
    @Override
    public boolean overdueGoalIsAchieved(SelfGoal goal) {
        GoalType goalType = goal.getGoalType();
        double depositValue = getDepositValueForPeriod(goal.getUser(), goal.getTicker(),
                goalType.getStartTime(END_OF_PREVIOUS_PERIOD), goalType.getStartTime(END_OF_CURRENT_PERIOD));

        goal.setCurrentAmount(depositValue);
        goal.setAchieved(depositValue > goal.getGoalAmount());
        return selfGoalRepository.save(goal).isAchieved();
    }

    private double getDepositValueForPeriod(User user, String ticker, LocalDateTime startingData, LocalDateTime endingDate) {
        return user.getBalances().stream()
                .map(Balance::getCode)
                .map(c -> getDepositValueForPeriodForSingleExchanger(user.getId(), ticker, startingData, endingDate, c))
                .reduce(0.0, Double::sum);
    }

    private double getDepositValueForPeriodForSingleExchanger(long userId, String ticker, LocalDateTime startingDate,
            LocalDateTime endingDate, ExchangerCode code) {

        return exchangerService.getWithdrawalDepositWalletHistory(userId, code)
                .stream()
                .filter(transaction -> transaction.getTicker().equalsIgnoreCase(ticker) &&
                        transaction.getTransferTransactionType().equals(TransferTransactionType.DEPOSIT) &&
                        transaction.getCreatedAt().isAfter(startingDate) && transaction.getCreatedAt().isBefore(endingDate))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
    }

    private void setPostQuantityValues(CryptoGoalsTableRecord entity) {
        BigDecimal goalQuantity = entity.getGoalQuantity();
        BigDecimal quantity = entity.getQuantity();

        BigDecimal leftToBuy = goalQuantity.subtract(quantity);

        entity.setLeftToBuy(leftToBuy.compareTo(BigDecimal.ZERO) >= 0 ? goalQuantity.subtract(quantity) : BigDecimal.ZERO);
        entity.setDonePercentage(quantity
                        .multiply(BigDecimal.valueOf(PERCENTAGE_SCALE))
                        .divide(goalQuantity, 1, RoundingMode.DOWN));
        entity.setFinished(quantity.compareTo(goalQuantity) >= 0);
    }

    CryptoGoalsTable saveCryptoTable(CryptoGoalsTable table) {
        CryptoGoalsTable createdTable = cryptoGoalsTableRepository.save(table);
        createdTable.getTableRecords().forEach(this::setPostQuantityValues);
        return createdTable;
    }

    private CryptoGoalsTable getCryptoGoalsTableOrThrowException(long tableId) {
        return cryptoGoalsTableRepository.findById(tableId)
                .orElseThrow(() -> new GoalsTableNotFoundException(String.format("CryptoGoalsTAble with id: %s was not found", tableId)));
    }
}

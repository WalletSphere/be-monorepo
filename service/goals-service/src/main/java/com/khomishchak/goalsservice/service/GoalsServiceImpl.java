package com.khomishchak.goalsservice.service;


import com.khomishchak.goalsservice.exception.GoalsTableNotFoundException;
import com.khomishchak.goalsservice.model.CommonGoalType;
import com.khomishchak.goalsservice.model.CryptoGoalTableTransaction;
import com.khomishchak.goalsservice.model.CryptoGoalsTable;
import com.khomishchak.goalsservice.model.CryptoGoalsTableRecord;
import com.khomishchak.goalsservice.model.GoalType;
import com.khomishchak.goalsservice.model.SelfGoal;
import com.khomishchak.goalsservice.model.TransactionChangeStateDTO;
import com.khomishchak.goalsservice.model.TransactionType;
import com.khomishchak.goalsservice.model.transaction.CreateNewRecordTransaction;
import com.khomishchak.goalsservice.repository.CryptoGoalsTableRepository;
import com.khomishchak.goalsservice.repository.SelfGoalRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GoalsServiceImpl implements GoalsService {

    public static final int PERCENTAGE_SCALE = 100;

    private static final String USER_ID = "userId";
    private static final String TICKER = "ticker";
    private static final String GOAL_START_DATE = "fromDate";
    private static final String GOAL_END_DATE = "toDate";


    private String depositWithdrawalTransactionsHistoryUrl;

    @Value("${ws.exchangers.authenticate.deposit-withdrawal-history.url:http://localhost:8080/balances/history/transactions/amount}")
    public void setDepositWithdrawalHistoryUrl(String depositWithdrawalTransactionsHistoryUrl) {
        this.depositWithdrawalTransactionsHistoryUrl = depositWithdrawalTransactionsHistoryUrl;
    }

    private final CryptoGoalsTableRepository cryptoGoalsTableRepository;
    private final SelfGoalRepository selfGoalRepository;
    private final Map<CommonGoalType, SelfGoalValidator> selfGoalValidators;
    private final RestTemplate restTemplate;

    public GoalsServiceImpl(CryptoGoalsTableRepository cryptoGoalsTableRepository,
                            SelfGoalRepository selfGoalRepository, List<SelfGoalValidator> selfGoalValidators,
                            RestTemplate restTemplate) {
        this.cryptoGoalsTableRepository = cryptoGoalsTableRepository;
        this.selfGoalRepository = selfGoalRepository;
        this.selfGoalValidators = selfGoalValidators.stream()
                .collect(Collectors.toMap(SelfGoalValidator::getCommonGoalType, validator -> validator));
        this.restTemplate = restTemplate;
    }

    @Override
    public CryptoGoalsTable createCryptoGoalsTable(Long userId, CryptoGoalsTable tableRequest) {
        tableRequest.setUserId(userId);
        return saveCryptoTable(tableRequest);
    }

    @Override
    public CryptoGoalsTable getCryptoGoalsTable(Long userId) {
        CryptoGoalsTable table  = cryptoGoalsTableRepository.findByUserId(userId).orElseThrow(
                () -> new GoalsTableNotFoundException(String.format("could not find goals table for userId: %d", userId)));
        table.getTableRecords().forEach(this::setPostQuantityValues);
        return table;
    }

    @Override
    public CryptoGoalsTable updateCryptoGoalsTable(CryptoGoalsTable cryptoGoalsTable, long userId) {
        cryptoGoalsTable.setUserId(userId);
        return saveCryptoTable(cryptoGoalsTable);
    }

    @Override
    public CryptoGoalsTable updateCryptoGoalsTable(CryptoGoalTableTransaction transaction, long tableId) {
        CryptoGoalsTable cryptoGoalsTable = getCryptoGoalsTableOrThrowException(tableId);
        updateCryptoGoalsTableWithSingleTransaction(cryptoGoalsTable, transaction);
        return saveCryptoTable(cryptoGoalsTable);
    }

    @Override
    public CryptoGoalsTable updateCryptoGoalsTable(CreateNewRecordTransaction transaction, long tableId) {
        CryptoGoalsTable table = getCryptoGoalsTableOrThrowException(tableId);
        table.addNewRecord(new CryptoGoalsTableRecord(transaction));
        return saveCryptoTable(table);
    }

    @Override
    public List<SelfGoal> saveAll(Iterable<SelfGoal> entities) {
        return selfGoalRepository.saveAll(entities);
    }

    @Override
    public List<SelfGoal> getAllOverdueGoals() {
        return selfGoalRepository.getAllOverdueGoals();
    }

    private void updateCryptoGoalsTableWithSingleTransaction(CryptoGoalsTable cryptoGoalsTable,
                                                             CryptoGoalTableTransaction transaction) {
        cryptoGoalsTable.getTableRecords().forEach(record -> applyTransactionToRecord(record, transaction));
    }

    private void applyTransactionToRecord(CryptoGoalsTableRecord record, CryptoGoalTableTransaction transaction) {
        if(!record.getName().equals(transaction.getTicker())) return;
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
                .newOperationAveragePrice(transaction.getPrice())
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
            goal.setCurrentAmount(requestForDepositValueForPeriod(userId, goal.getTicker(), goal.getStartDate(), goal.getEndDate()));
            goal.setAchieved(goal.getCurrentAmount() > goal.getGoalAmount());
        });
        return result;
    }

    @Override
    @Transactional
    public List<SelfGoal> createSelfGoals(Long userId, List<SelfGoal> goals) {
        goals.forEach(g -> {
            GoalType goalType = g.getGoalType();
            g.setUserId(userId);
            g.setStartDate(goalType.getStartTime(1));
            g.setEndDate(goalType.getEndTime());
        });
        return selfGoalRepository.saveAll(goals);
    }

    public boolean overdueGoalIsAchieved(SelfGoal goal) {
        return selfGoalValidators.get(goal.getGoalType().getCommonType()).isAchieved(goal);
    }

    //TODO: will be refactored once we will start using kafka for deposit updates
    // webhooks will be sending us the updates regarding the deposit amount, we will listen to it and set new current value to goal
    private double requestForDepositValueForPeriod(long userId, String ticker, LocalDateTime startingDate,
                                            LocalDateTime endingDate) {
        // temporary change to be able to send request to balance service without passing token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(USER_ID, String.valueOf(userId));

        URI targetUrl = UriComponentsBuilder.fromUriString(depositWithdrawalTransactionsHistoryUrl)
                .queryParam(TICKER, ticker)
                .queryParam(GOAL_START_DATE, startingDate.toString())
                .queryParam(GOAL_END_DATE, endingDate.toString())
                .queryParam("transferTransactionType", "DEPOSIT")
                .queryParam("transactionStatus", "COMPLETED")
                .build().encode().toUri();
        return restTemplate.exchange(targetUrl, HttpMethod.GET, new HttpEntity<String>(headers), Double.class).getBody();
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
                .orElseThrow(() -> new GoalsTableNotFoundException(String.format("CryptoGoalsTable with id: %s was not found", tableId)));
    }
}
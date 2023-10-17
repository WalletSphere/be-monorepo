package com.khomishchak.cryptoportfolio.services;

import com.khomishchak.cryptoportfolio.exceptions.GoalsTableNotFoundException;
import com.khomishchak.cryptoportfolio.exceptions.UserNotFoundException;
import com.khomishchak.cryptoportfolio.model.Transaction;
import com.khomishchak.cryptoportfolio.model.TransactionType;
import com.khomishchak.cryptoportfolio.model.User;
import com.khomishchak.cryptoportfolio.model.enums.ExchangerCode;
import com.khomishchak.cryptoportfolio.model.goals.CryptoGoalsTableRecord;
import com.khomishchak.cryptoportfolio.model.goals.CryptoGoalsRecordUpdateReq;
import com.khomishchak.cryptoportfolio.model.goals.CryptoGoalsTable;
import com.khomishchak.cryptoportfolio.model.enums.GoalType;
import com.khomishchak.cryptoportfolio.model.goals.SelfGoal;
import com.khomishchak.cryptoportfolio.repositories.CryptoGoalsTableRepository;
import com.khomishchak.cryptoportfolio.repositories.SelfGoalRepository;
import com.khomishchak.cryptoportfolio.services.exchangers.ExchangerService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GoalsServiceImpl implements GoalsService {

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
    public CryptoGoalsTable getCryptoGoalsTable(Long accountId) {
        CryptoGoalsTable table  = userService.getUserById(accountId).getCryptoGoalsTable();

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
    public CryptoGoalsTable updateCryptoGoalsTableRecords(List<CryptoGoalsRecordUpdateReq> recordUpdateReq, long tableId) {
       CryptoGoalsTable cryptoGoalsTable = getCryptoGoalsTableOrThrowException(tableId);

       recordUpdateReq.forEach(record -> {
           CryptoGoalsTableRecord tableRecord = cryptoGoalsTable.getTableRecords().stream()
                   .filter(r -> r.getName().equals(record.ticker()))
                   .findFirst()
                   .orElseThrow(() -> new RuntimeException("fsa"));

           tableRecord.setAverageCost(tableRecord.getAverageCost()
                   .multiply(tableRecord.getQuantity())
                   .add(record.price().multiply(BigDecimal.valueOf(record.amount())))
                   .divide(tableRecord.getQuantity().add(BigDecimal.valueOf(record.amount())),4, RoundingMode.DOWN));
           tableRecord.setQuantity(tableRecord.getQuantity().add(BigDecimal.valueOf(record.amount())));
       });

       return saveCryptoTable(cryptoGoalsTable);
    }

    @Override
    public List<SelfGoal> getSelfGoals(Long accountId) {

        List<SelfGoal> result = selfGoalRepository.findAllByUserId(accountId);

        result.forEach(goal -> {
            goal.setCurrentAmount(getDepositValueForPeriod(accountId, goal.getTicker(), goal.getStartDate(), goal.getEndDate()));
            goal.setAchieved(goal.getCurrentAmount() > goal.getGoalAmount());
    });
        return result;
    }

    @Override
    @Transactional
    public List<SelfGoal> createSelfGoals(Long accountId, List<SelfGoal> goals) {
        User user = userService.getUserById(accountId);
        user.setSelfGoals(goals);

        goals.forEach(g -> {
            g.setUser(user);
            g.setStartDate(LocalDateTime.now());
            g.setEndDate(LocalDateTime.now().plusMinutes(1));//g.getGoalType().getEndTime());
            g.setAchieved(g.getCurrentAmount() > g.getGoalAmount());
            g.setCurrentAmount(getDepositValueForPeriod(accountId, g.getTicker(), g.getStartDate(), g.getEndDate()));
        });

        userService.saveUser(user);

        return goals;
    }

    // TODO: should be replaced with strategy pattern to handle multiple goal types, not only deposit
    @Override
    public boolean overdueGoalIsAchieved(SelfGoal goal) {
        GoalType goalType = goal.getGoalType();
        double depositValue = getDepositValueForPeriod(goal.getUser().getId(), goal.getTicker(),
                goalType.getStartTime(2), goalType.getStartTime(1));

        goal.setCurrentAmount(depositValue);
        goal.setAchieved(depositValue > goal.getGoalAmount());
        return selfGoalRepository.save(goal).isAchieved();
    }

    private double getDepositValueForPeriod(long accountId, String ticker, LocalDateTime startingData, LocalDateTime endingDate) {
        return exchangerService.getWithdrawalDepositWalletHistory(accountId, ExchangerCode.WHITE_BIT)
                .stream()
                .filter(transaction -> transaction.getTicker().equalsIgnoreCase(ticker) &&
                        transaction.getTransactionType().equals(TransactionType.WITHDRAWAL) &&
                        transaction.getCreatedAt().isAfter(startingData) && transaction.getCreatedAt().isBefore(endingDate))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
    }

    private CryptoGoalsTableRecord setPostQuantityValues(CryptoGoalsTableRecord entity) {
        BigDecimal goalQuantity = entity.getGoalQuantity();
        BigDecimal quantity = entity.getQuantity();

        BigDecimal leftToBuy = goalQuantity.subtract(quantity);

        entity.setLeftToBuy(leftToBuy.compareTo(BigDecimal.ZERO) >= 0 ? goalQuantity.subtract(quantity) : BigDecimal.ZERO);
        entity.setDonePercentage(quantity
                        .multiply(BigDecimal.valueOf(100))
                        .divide(goalQuantity, 1, RoundingMode.DOWN));
        entity.setFinished(quantity.compareTo(goalQuantity) >= 0);

        return entity;
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

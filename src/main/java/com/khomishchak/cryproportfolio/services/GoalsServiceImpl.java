package com.khomishchak.cryproportfolio.services;

import com.khomishchak.cryproportfolio.exceptions.GoalsTableNotFoundException;
import com.khomishchak.cryproportfolio.exceptions.UserNotFoundException;
import com.khomishchak.cryproportfolio.model.User;
import com.khomishchak.cryproportfolio.model.goals.CryptoGoalsTableRecord;
import com.khomishchak.cryproportfolio.model.goals.CryptoGoalsRecordUpdateReq;
import com.khomishchak.cryproportfolio.model.goals.CryptoGoalsTable;
import com.khomishchak.cryproportfolio.repositories.CryptoGoalsTableRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class GoalsServiceImpl implements GoalsService {

    private final CryptoGoalsTableRepository cryptoGoalsTableRepository;
    private final UserService userService;

    public GoalsServiceImpl(CryptoGoalsTableRepository cryptoGoalsTableRepository, UserService userService) {
        this.cryptoGoalsTableRepository = cryptoGoalsTableRepository;
        this.userService = userService;
    }

    @Override
    public CryptoGoalsTable createCryptoGoalsTable(Long userId, CryptoGoalsTable tableRequest) {

        User user = getUserOrThrowException(userId);
        user.setCryptoGoalsTable(tableRequest);
        tableRequest.setUser(user);

        return saveCryptoTable(tableRequest);
    }

    @Override
    public CryptoGoalsTable getCryptoGoalsTable(Long accountId) {
        CryptoGoalsTable table  = getUserOrThrowException(accountId).getCryptoGoalsTable();

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


    private User getUserOrThrowException(long userId) {
        return userService.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id: %s was not found", userId)));
    }

    private CryptoGoalsTable getCryptoGoalsTableOrThrowException(long tableId) {
        return cryptoGoalsTableRepository.findById(tableId)
                .orElseThrow(() -> new GoalsTableNotFoundException(String.format("CryptoGoalsTAble with id: %s was not found", tableId)));
    }
}

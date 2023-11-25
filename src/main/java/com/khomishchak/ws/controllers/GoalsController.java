package com.khomishchak.ws.controllers;

import com.khomishchak.ws.model.goals.CryptoGoalTableTransaction;
import com.khomishchak.ws.model.goals.CryptoGoalsTable;
import com.khomishchak.ws.model.goals.SelfGoal;
import com.khomishchak.ws.services.GoalsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/goals")
public class GoalsController {

    private final GoalsService goalsService;

    public GoalsController(GoalsService goalsService) {
        this.goalsService = goalsService;
    }

    @PostMapping("/crypto-tables")
    public ResponseEntity<CryptoGoalsTable> createCryptoGoalsTable(@RequestAttribute Long userId,
            @RequestBody CryptoGoalsTable requestTable) {
        return new ResponseEntity<>(goalsService.createCryptoGoalsTable(userId, requestTable), HttpStatus.CREATED);
    }

    @GetMapping("/crypto-tables")
    public ResponseEntity<CryptoGoalsTable> getCryptoGoalsTable(@RequestAttribute Long userId) {
        return new ResponseEntity<>(goalsService.getCryptoGoalsTable(userId), HttpStatus.OK);
    }

    @PutMapping("/crypto-tables")
    public ResponseEntity<CryptoGoalsTable> updateWholeCryptoGoalsTable(@RequestBody CryptoGoalsTable cryptoGoalsTable) {
        return new ResponseEntity<>(goalsService.updateCryptoGoalsTable(cryptoGoalsTable), HttpStatus.OK);
    }

    @PutMapping("/{tableId}/crypto-tables")
    public ResponseEntity<CryptoGoalsTable> updateCryptoGoalsTableWithSingleTransaction(
            @RequestBody CryptoGoalTableTransaction transaction, @PathVariable Long tableId) {
        return new ResponseEntity<>(goalsService.updateCryptoGoalsTable(transaction, tableId), HttpStatus.OK);
    }

    @GetMapping("/self-goals")
    public ResponseEntity<List<SelfGoal>> getSelfGoals(@RequestAttribute Long userId) {
        return new ResponseEntity<>(goalsService.getSelfGoals(userId), HttpStatus.OK);
    }

    @PostMapping("/self-goals")
    public ResponseEntity<List<SelfGoal>> createSelfGoals(@RequestAttribute Long userId, @RequestBody List<SelfGoal> goals) {

        return new ResponseEntity<>(goalsService.createSelfGoals(userId, goals), HttpStatus.OK);
    }
}

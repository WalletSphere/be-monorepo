package com.khomishchak.cryptoportfolio.controllers;

import com.khomishchak.cryptoportfolio.model.goals.CryptoGoalsRecordUpdateReq;
import com.khomishchak.cryptoportfolio.model.goals.CryptoGoalsTable;
import com.khomishchak.cryptoportfolio.model.goals.SelfGoal;
import com.khomishchak.cryptoportfolio.services.GoalsService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import jakarta.websocket.server.PathParam;
import lombok.Getter;


@RestController
@RequestMapping("/api/v1/goals")
public class GoalsController {

    private final GoalsService goalsService;

    public GoalsController(GoalsService goalsService) {
        this.goalsService = goalsService;
    }

    @PostMapping("/crypto-tables")
    public ResponseEntity<CryptoGoalsTable> createCryptoGoalsTable(@RequestParam Long accountId,
            @RequestBody CryptoGoalsTable requestTable) {
        return new ResponseEntity<>(goalsService.createCryptoGoalsTable(accountId, requestTable), HttpStatus.CREATED);
    }

    @GetMapping("/crypto-tables")
    public ResponseEntity<CryptoGoalsTable> getCryptoGoalsTable(@RequestParam Long accountId) {
        return new ResponseEntity<>(goalsService.getCryptoGoalsTable(accountId), HttpStatus.OK);
    }

    @PutMapping("/crypto-tables")
    public ResponseEntity<CryptoGoalsTable> updateCryptoGoalsTable(@RequestBody CryptoGoalsTable cryptoGoalsTable) {
        return new ResponseEntity<>(goalsService.updateCryptoGoalsTable(cryptoGoalsTable), HttpStatus.OK);
    }

    @PutMapping("/crypto-tables/{tableId}/records")
    public ResponseEntity<CryptoGoalsTable> updateCryptoGoalsTableRecord(
            @RequestBody List<CryptoGoalsRecordUpdateReq> recordUpdateReq, @PathVariable Long tableId) {
        return new ResponseEntity<>(goalsService.updateCryptoGoalsTableRecords(recordUpdateReq, tableId), HttpStatus.OK);
    }

    @GetMapping("/self-goals")
    public ResponseEntity<List<SelfGoal>> getSelfGoals(@RequestParam Long accountId) {
        return new ResponseEntity<>(goalsService.getSelfGoals(accountId), HttpStatus.OK);
    }

    @PostMapping("/self-goals")
    public ResponseEntity<List<SelfGoal>> createSelfGoals(@RequestParam Long accountId,
            @RequestBody List<SelfGoal> goals) {

        return new ResponseEntity<>(goalsService.createSelfGoals(accountId, goals), HttpStatus.OK);
    }
}

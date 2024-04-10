package com.khomishchak.ws.controllers;

import com.khomishchak.ws.services.exchangers.ExchangerService;
import com.khomishchak.ws.model.filter.TransactionSearchCriteria;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/balances")
public class BalanceController {

    private final ExchangerService exchangerService;

    public BalanceController(ExchangerService exchangerService) {
        this.exchangerService = exchangerService;
    }

    @GetMapping("/history/transactions/amount")
    public Double getDepositAmountForPeriod(@RequestHeader("UserId") Long userId, TransactionSearchCriteria searchCriteria) {
        return exchangerService.getDepositValueForPeriod(userId, searchCriteria);
    }
}

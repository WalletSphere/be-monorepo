package com.khomishchak.ws.controllers;

import com.khomishchak.ws.services.exchangers.ExchangerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/balances")
public class BalanceController {

    private final ExchangerService exchangerService;

    public BalanceController(ExchangerService exchangerService) {
        this.exchangerService = exchangerService;
    }

    @GetMapping("/transactions-history/period")
    public Double getDepositAmountForPeriod(@RequestParam Long userId, @RequestParam String ticker,
                                            @RequestParam LocalDateTime startingDate,
                                            @RequestParam LocalDateTime endingDate) {
        return exchangerService.getDepositValueForPeriod(userId, ticker, startingDate, endingDate);
    }
}

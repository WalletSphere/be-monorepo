package com.khomishchak.CryproPortfolio.controllers;

import com.khomishchak.CryproPortfolio.model.User;
import com.khomishchak.CryproPortfolio.model.enums.ExchangerCode;
import com.khomishchak.CryproPortfolio.model.exchanger.Balance;
import com.khomishchak.CryproPortfolio.model.requests.RegisterBalanceReq;
import com.khomishchak.CryproPortfolio.services.ExchangersService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exchangers")
public class ExchangerController {

    private final ExchangersService exchangersService;

    public ExchangerController(ExchangersService exchangersService) {
        this.exchangersService = exchangersService;
    }

    @PostMapping("/balance/{accountId}/add")
    public User addExchangerForUser(@PathVariable Long accountId, @RequestBody RegisterBalanceReq balanceReq) {
        return exchangersService.persistExchangerBalanceForUser(balanceReq.publicKey(), balanceReq.secretKey() ,accountId,
                balanceReq.code());
    }

    @GetMapping("/balance/{accountId}/get/{exchangerCode}")
    public Balance getAccountBalance(@PathVariable long accountId, @PathVariable String exchangerCode) {
        return exchangersService.getMainBalance(accountId, ExchangerCode.valueOf(exchangerCode));
    }

    @GetMapping("/balance/{accountId}/get/all")
    public List<Balance> getAccountBalances(@PathVariable long accountId) {
        return exchangersService.getAllMainBalances(accountId);
    }
}

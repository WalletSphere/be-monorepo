package com.khomishchak.cryproportfolio.controllers;

import com.khomishchak.cryproportfolio.model.User;
import com.khomishchak.cryproportfolio.model.enums.ExchangerCode;
import com.khomishchak.cryproportfolio.model.exchanger.Balance;
import com.khomishchak.cryproportfolio.model.requests.RegisterBalanceReq;
import com.khomishchak.cryproportfolio.services.exchangers.ExchangerService;

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

    private final ExchangerService exchangerService;

    public ExchangerController(ExchangerService exchangerService) {
        this.exchangerService = exchangerService;
    }

    @PostMapping("/balance/{accountId}/add")
    public User addExchangerForUser(@PathVariable Long accountId, @RequestBody RegisterBalanceReq balanceReq) {
        return exchangerService.persistExchangerBalanceForUser(balanceReq.publicKey(), balanceReq.secretKey() ,accountId,
                balanceReq.code());
    }

    @GetMapping("/balance/{accountId}/get/{exchangerCode}")
    public Balance getAccountBalance(@PathVariable long accountId, @PathVariable String exchangerCode) {
        return exchangerService.getMainBalance(accountId, ExchangerCode.valueOf(exchangerCode));
    }

    @GetMapping("/balance/{accountId}/get/all")
    public List<Balance> getAccountBalances(@PathVariable long accountId) {
        return exchangerService.getAllMainBalances(accountId);
    }
}

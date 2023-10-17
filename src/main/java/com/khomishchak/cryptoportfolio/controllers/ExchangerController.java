package com.khomishchak.cryptoportfolio.controllers;

import com.khomishchak.cryptoportfolio.model.DepositWithdrawalTransaction;
import com.khomishchak.cryptoportfolio.model.User;
import com.khomishchak.cryptoportfolio.model.enums.ExchangerCode;
import com.khomishchak.cryptoportfolio.model.exchanger.Balance;
import com.khomishchak.cryptoportfolio.model.requests.RegisterBalanceReq;
import com.khomishchak.cryptoportfolio.services.exchangers.ExchangerService;

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

    // TODO: update return type to dto
    @PostMapping("/balance/{accountId}/add")
    public User addExchangerForUser(@PathVariable Long accountId, @RequestBody RegisterBalanceReq balanceReq) {
        return exchangerService.persistExchangerBalanceForUser(balanceReq.publicKey(), balanceReq.secretKey() ,accountId,
                balanceReq.code());
    }

    @GetMapping("/balance/{accountId}/{exchangerCode}")
    public Balance getAccountBalance(@PathVariable long accountId, @PathVariable String exchangerCode) {
        return exchangerService.getMainBalance(accountId, ExchangerCode.valueOf(exchangerCode));
    }

    @GetMapping("/balance/{accountId}/all")
    public List<Balance> getAccountBalances(@PathVariable long accountId) {
        return exchangerService.getAllMainBalances(accountId);
    }

    @GetMapping("/wallet/{accountId}/deposit-withdrawal-history/{exchangerCode}")
    public List<DepositWithdrawalTransaction> getAccountWithdrawalDepositWalletHistory(@PathVariable long accountId,
            @PathVariable String exchangerCode) {
        return exchangerService.getWithdrawalDepositWalletHistory(accountId, ExchangerCode.valueOf(exchangerCode));
    }
}

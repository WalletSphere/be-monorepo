package com.khomishchak.cryptoportfolio.controllers;

import com.khomishchak.cryptoportfolio.model.DepositWithdrawalTransaction;
import com.khomishchak.cryptoportfolio.model.enums.ExchangerCode;
import com.khomishchak.cryptoportfolio.model.exchanger.Balance;
import com.khomishchak.cryptoportfolio.model.requests.RegisterApiKeysReq;
import com.khomishchak.cryptoportfolio.model.response.RegisterApiKeysResp;
import com.khomishchak.cryptoportfolio.services.exchangers.ExchangerService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
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

    @PostMapping("/api-keys")
    public RegisterApiKeysResp addExchangerApiKeysForUser(@RequestAttribute Long userId, @RequestBody RegisterApiKeysReq balanceReq) {
        return exchangerService.persistExchangerApiKeysForUser(balanceReq.publicKey(), balanceReq.secretKey() , userId,
                balanceReq.code());
    }

    @GetMapping("/balance/{exchangerCode}")
    public Balance getAccountBalance(@RequestAttribute Long userId, @PathVariable String exchangerCode) {
        return exchangerService.getMainBalance(userId, ExchangerCode.valueOf(exchangerCode));
    }

    @GetMapping("/balance/all")
    public List<Balance> getAccountBalances(@RequestAttribute long userId) {
        return exchangerService.getAllMainBalances(userId);
    }

    @GetMapping("/wallet/deposit-withdrawal-history/{exchangerCode}")
    public List<DepositWithdrawalTransaction> getAccountWithdrawalDepositWalletHistory(@RequestAttribute long userId,
            @PathVariable String exchangerCode) {
        return exchangerService.getWithdrawalDepositWalletHistory(userId, ExchangerCode.valueOf(exchangerCode));
    }
}

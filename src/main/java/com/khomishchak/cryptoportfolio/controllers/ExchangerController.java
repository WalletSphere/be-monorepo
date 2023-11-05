package com.khomishchak.cryptoportfolio.controllers;

import com.khomishchak.cryptoportfolio.model.DepositWithdrawalTransaction;
import com.khomishchak.cryptoportfolio.model.enums.ExchangerCode;
import com.khomishchak.cryptoportfolio.model.exchanger.Balance;
import com.khomishchak.cryptoportfolio.model.requests.RegisterExchangerInfoReq;
import com.khomishchak.cryptoportfolio.model.response.DeleteExchangerResp;
import com.khomishchak.cryptoportfolio.model.response.FirstlyGeneratedBalanceResp;
import com.khomishchak.cryptoportfolio.model.response.SyncDataResp;
import com.khomishchak.cryptoportfolio.services.exchangers.ExchangerService;

import org.springframework.web.bind.annotation.DeleteMapping;
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
    public FirstlyGeneratedBalanceResp addExchangerApiKeysForUser(@RequestAttribute Long userId,
                                                                  @RequestBody RegisterExchangerInfoReq exchangerInfoReq) {
        return exchangerService.addGeneralExchangerInfo(exchangerInfoReq , userId);
    }

    @GetMapping("/{exchangerCode}/balance")
    public Balance getAccountBalance(@RequestAttribute Long userId, @PathVariable String exchangerCode) {
        return exchangerService.getMainBalance(userId, ExchangerCode.valueOf(exchangerCode));
    }

    @GetMapping("/balance/all")
    public List<Balance> getAccountBalances(@RequestAttribute long userId) {
        return exchangerService.getAllMainBalances(userId);
    }

    @DeleteMapping("/{exchangerCode}/balance")
    public DeleteExchangerResp deleteAccountBalance(@RequestAttribute long userId, @PathVariable String exchangerCode) {
        return exchangerService.deleteExchangerForUser(userId, ExchangerCode.valueOf(exchangerCode));
    }

    @GetMapping("/{exchangerCode}/wallet/deposit-withdrawal-history")
    public List<DepositWithdrawalTransaction> getAccountWithdrawalDepositWalletHistory(@RequestAttribute long userId,
            @PathVariable String exchangerCode) {
        return exchangerService.getWithdrawalDepositWalletHistory(userId, ExchangerCode.valueOf(exchangerCode));
    }

    // TODO: consider to create a separate controller for sync functionality
    @PostMapping("/synchronize/balance")
    public SyncDataResp synchronizeBalanceDataForUser(@RequestAttribute Long userId) {
        return exchangerService.synchronizeBalanceDataForUser(userId);
    }

    // TODO: create sync API for all exchanger related data
}

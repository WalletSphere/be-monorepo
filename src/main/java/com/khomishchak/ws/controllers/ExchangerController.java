package com.khomishchak.ws.controllers;

import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.model.exchanger.Balance;
import com.khomishchak.ws.model.exchanger.ExchangerUniqueCurrenciesDTO;
import com.khomishchak.ws.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import com.khomishchak.ws.model.requests.RegisterExchangerInfoReq;
import com.khomishchak.ws.model.response.FirstlyGeneratedBalanceResp;
import com.khomishchak.ws.model.response.SyncBalancesResp;
import com.khomishchak.ws.model.response.SyncDepositWithdrawalTransactionsResp;
import com.khomishchak.ws.services.exchangers.ExchangerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exchangers")
public class ExchangerController {

    private final ExchangerService exchangerService;

    public ExchangerController(ExchangerService exchangerService) {
        this.exchangerService = exchangerService;
    }

    @PostMapping("/api-keys")
    public FirstlyGeneratedBalanceResp addExchangerApiKeysForUser(@RequestHeader("UserId") Long userId,
                                                                  @RequestBody RegisterExchangerInfoReq exchangerInfoReq) {
        return exchangerService.addGeneralExchangerInfo(exchangerInfoReq , userId);
    }

    @GetMapping("/{exchangerCode}/balance")
    public Balance getAccountBalance(@RequestHeader("UserId") Long userId, @PathVariable String exchangerCode) {
        return exchangerService.getMainBalance(userId, ExchangerCode.valueOf(exchangerCode));
    }

    @GetMapping("/balance/all")
    public List<Balance> getAccountBalances(@RequestHeader("UserId") long userId) {
        return exchangerService.getAllMainBalances(userId);
    }

    @DeleteMapping("/balances/{balanceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccountBalance(@PathVariable long balanceId) {
        exchangerService.deleteExchangerForUser(balanceId);
    }

    @GetMapping("/deposit-withdrawal-history")
    public List<ExchangerDepositWithdrawalTransactions> getWithdrawalDepositTransactionsHistory(@RequestHeader("UserId") long userId) {
        return exchangerService.getWithdrawalDepositWalletHistory(userId);
    }

    @PostMapping("/synchronize/balance")
    public SyncBalancesResp synchronizeBalanceDataForUser(@RequestHeader("UserId") Long userId) {
        return exchangerService.synchronizeBalanceDataForUser(userId);
    }

    @PostMapping("/deposit-withdrawal-history/synchronize")
    public SyncDepositWithdrawalTransactionsResp synchronizeDWTransactionsHistory(@RequestHeader("UserId") Long userId) {
        return exchangerService.synchronizeDepositWithdrawalTransactionsData(userId);
    }

    @GetMapping("/used-currencies")
    public List<ExchangerUniqueCurrenciesDTO> getCurrencies() {
        return exchangerService.getUsedCurrencies();
    }
}

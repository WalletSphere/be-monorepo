package com.walletsphere.wsmonolith.services.exchangers;

import com.walletsphere.wsmonolith.model.User;
import com.walletsphere.wsmonolith.model.enums.ExchangerCode;
import com.walletsphere.wsmonolith.model.enums.RegistrationStatus;
import com.walletsphere.wsmonolith.model.exchanger.ApiKeySetting;
import com.walletsphere.wsmonolith.model.exchanger.ApiKeysPair;
import com.walletsphere.wsmonolith.model.exchanger.Balance;
import com.walletsphere.wsmonolith.model.exchanger.ExchangerUniqueCurrenciesDTO;
import com.walletsphere.wsmonolith.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import com.walletsphere.wsmonolith.model.exchanger.transaction.Transaction;
import com.walletsphere.wsmonolith.model.requests.RegisterApiKeysReq;
import com.walletsphere.wsmonolith.model.requests.RegisterExchangerInfoReq;
import com.walletsphere.wsmonolith.model.response.FirstlyGeneratedBalanceResp;
import com.walletsphere.wsmonolith.repositories.ApiKeySettingRepository;
import com.walletsphere.wsmonolith.services.UserService;
import com.walletsphere.wsmonolith.services.exchangers.balances.BalanceService;
import com.walletsphere.wsmonolith.services.exchangers.balances.history.AccountBalanceTransferOperationsHistoryService;
import com.walletsphere.wsmonolith.model.filter.TransactionFilter;
import com.walletsphere.wsmonolith.model.filter.TransactionSearchCriteria;
import com.walletsphere.wsmonolith.services.security.encryption.AesEncryptionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ExchangerServiceImpl implements ExchangerService {

    private final UserService userService;
    private final ApiKeySettingRepository apiKeySettingRepository;
    private final AesEncryptionService aesEncryptionService;
    private final BalanceService balanceService;
    private final AccountBalanceTransferOperationsHistoryService accountBalanceTransferOperationsHistoryService;

    public ExchangerServiceImpl(UserService userService, ApiKeySettingRepository apiKeySettingRepository,
                                AesEncryptionService aesEncryptionService, BalanceService balanceService,
                                AccountBalanceTransferOperationsHistoryService accountBalanceTransferOperationsHistoryService) {
        this.userService = userService;
        this.apiKeySettingRepository = apiKeySettingRepository;
        this.aesEncryptionService = aesEncryptionService;
        this.balanceService = balanceService;
        this.accountBalanceTransferOperationsHistoryService = accountBalanceTransferOperationsHistoryService;
    }

    @Override
    @Transactional
    public FirstlyGeneratedBalanceResp addGeneralExchangerInfo(RegisterExchangerInfoReq exchangerInfoReq, long userId) {
        User user = userService.getUserById(userId);
        RegisterApiKeysReq apiKeys = exchangerInfoReq.apiKeysReq();
        ExchangerCode code = exchangerInfoReq.code();

        persistApiKeysSettings(user, apiKeys.secretKey(), apiKeys.publicKey(), code);

        Balance emptyBalance = balanceService.registerBalanceEntryInfo(code, exchangerInfoReq.balanceName(), user);

        return new FirstlyGeneratedBalanceResp(emptyBalance.getId(), userId, RegistrationStatus.SUCCESSFUL);
    }

    @Override
    public Balance getMainBalance(long userId, ExchangerCode exchangerCode) {
        return balanceService.getMainBalance(userId, exchangerCode);
    }

    @Override
    public List<Balance> getAllMainBalances(long userId) {
        return balanceService.getMainBalances(userId);
    }

    @Override
    public double getDepositValueForPeriod(long userId, TransactionSearchCriteria searchCriteria) {
        return getWithdrawalDepositWalletHistory(userId).stream()
                .map(transactions ->
                        getDepositValueForPeriodForSingleIntegratedBalance(transactions, searchCriteria))
                .reduce(0.0, Double::sum);
    }

    private double getDepositValueForPeriodForSingleIntegratedBalance(ExchangerDepositWithdrawalTransactions transactions,
                                                                      TransactionSearchCriteria searchCriteria) {
        return TransactionFilter.filterTransactions(transactions.getTransactions(), searchCriteria).stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
    }

    @Override
    public List<ExchangerDepositWithdrawalTransactions> getWithdrawalDepositWalletHistory(long userId) {
        return accountBalanceTransferOperationsHistoryService.getDepositWithdrawalTransactionsHistory(userId);
    }

    @Override
    public List<Balance> synchronizeBalanceDataForUser(long userId) {
        return balanceService.synchronizeBalances(userId);
    }

    @Override
    public List<ExchangerDepositWithdrawalTransactions> synchronizeDepositWithdrawalTransactionsData(long userId) {
        return accountBalanceTransferOperationsHistoryService.synchronizeDepositWithdrawalTransactionsHistory(userId);
    }

    @Override
    public void deleteExchangerForUser(long balanceId) {
        balanceService.deleteBalance(balanceId);
    }

    @Override
    public List<ExchangerUniqueCurrenciesDTO> getUsedCurrencies() {
        return balanceService.getUsedCurrencies();
    }

    private void persistApiKeysSettings(User user, String privateKey, String publicApi, ExchangerCode code) {
        ApiKeysPair apiKeysPair = ApiKeysPair.builder()
                .publicApi(aesEncryptionService.encrypt(publicApi))
                .privateKey(aesEncryptionService.encrypt(privateKey))
                .build();

        ApiKeySetting apiKeySetting = ApiKeySetting.
                builder()
                .user(user)
                .code(code)
                .apiKeys(apiKeysPair)
                .build();

        user.getApiKeysSettings().add(apiKeySetting);

        apiKeySettingRepository.save(apiKeySetting);
    }
}

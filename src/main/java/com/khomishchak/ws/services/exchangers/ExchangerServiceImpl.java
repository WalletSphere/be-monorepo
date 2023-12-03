package com.khomishchak.ws.services.exchangers;

import com.khomishchak.ws.model.User;
import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.model.enums.RegistrationStatus;
import com.khomishchak.ws.model.exchanger.ApiKeySetting;
import com.khomishchak.ws.model.exchanger.ApiKeysPair;
import com.khomishchak.ws.model.exchanger.Balance;
import com.khomishchak.ws.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import com.khomishchak.ws.model.requests.RegisterApiKeysReq;
import com.khomishchak.ws.model.requests.RegisterExchangerInfoReq;
import com.khomishchak.ws.model.response.FirstlyGeneratedBalanceResp;
import com.khomishchak.ws.model.response.SyncBalancesResp;
import com.khomishchak.ws.model.response.SyncDepositWithdrawalTransactionsResp;
import com.khomishchak.ws.repositories.ApiKeySettingRepository;
import com.khomishchak.ws.services.UserService;
import com.khomishchak.ws.services.exchangers.balances.BalanceService;
import com.khomishchak.ws.services.exchangers.balances.history.AccountBalanceTransferOperationsHistoryService;
import com.khomishchak.ws.services.security.encryption.AesEncryptionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
    public List<ExchangerDepositWithdrawalTransactions> getWithdrawalDepositWalletHistory(long userId) {
        return accountBalanceTransferOperationsHistoryService.getDepositWithdrawalTransactionsHistory(userId);
    }

    @Override
    public SyncBalancesResp synchronizeBalanceDataForUser(long userId) {
        return new SyncBalancesResp(balanceService.synchronizeBalances(userId));
    }

    @Override
    public SyncDepositWithdrawalTransactionsResp synchronizeDepositWithdrawalTransactionsData(long userId) {
        return new SyncDepositWithdrawalTransactionsResp(
                accountBalanceTransferOperationsHistoryService.synchronizeDepositWithdrawalTransactionsHistory(userId));
    }

    @Override
    public void deleteExchangerForUser(long balanceId) {
        balanceService.deleteBalance(balanceId);
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

package com.khomishchak.ws.services.exchangers;

import com.khomishchak.ws.model.DepositWithdrawalTransaction;
import com.khomishchak.ws.model.User;
import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.model.enums.RegistrationStatus;
import com.khomishchak.ws.model.exchanger.ApiKeySetting;
import com.khomishchak.ws.model.exchanger.ApiKeysPair;
import com.khomishchak.ws.model.exchanger.Balance;
import com.khomishchak.ws.model.requests.RegisterApiKeysReq;
import com.khomishchak.ws.model.requests.RegisterExchangerInfoReq;
import com.khomishchak.ws.model.response.DeleteExchangerResp;
import com.khomishchak.ws.model.response.FirstlyGeneratedBalanceResp;
import com.khomishchak.ws.model.response.RegisterApiKeysResp;
import com.khomishchak.ws.model.response.SyncDataResp;
import com.khomishchak.ws.repositories.ApiKeySettingRepository;
import com.khomishchak.ws.repositories.UserRepository;
import com.khomishchak.ws.services.exchangers.balances.BalanceService;
import com.khomishchak.ws.services.security.encryption.AesEncryptionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExchangerServiceImpl implements ExchangerService {

    private final UserRepository userRepository;
    private final ApiKeySettingRepository apiKeySettingRepository;
    private final AesEncryptionService aesEncryptionService;
    private final BalanceService balanceService;
    private final Map<ExchangerCode, ExchangerConnectorServiceFactory> exchangerServiceFactories;

    public ExchangerServiceImpl(UserRepository userRepository, ApiKeySettingRepository apiKeySettingRepository,
                                AesEncryptionService aesEncryptionService,
                                List<ExchangerConnectorServiceFactory> exchangerServiceFactories,
                                BalanceService balanceService) {
        this.userRepository = userRepository;
        this.apiKeySettingRepository = apiKeySettingRepository;
        this.aesEncryptionService = aesEncryptionService;
        this.balanceService = balanceService;
        this.exchangerServiceFactories = exchangerServiceFactories.stream()
                .collect(Collectors.toMap(ExchangerConnectorServiceFactory::getExchangerCode, factory -> factory));
    }

    @Override
    @Transactional
    public FirstlyGeneratedBalanceResp addGeneralExchangerInfo(RegisterExchangerInfoReq exchangerInfoReq, long userId) {
        User user = userRepository.getReferenceById(userId);
        RegisterApiKeysReq apiKeys = exchangerInfoReq.apiKeysReq();
        ExchangerCode code = exchangerInfoReq.code();

        generateApiKeysSettingsForUser(user, apiKeys.secretKey(), apiKeys.publicKey(), code);

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

    // TODO: should be moved to another service layer that will handle cache and synchronize implementations
    @Override
    public List<DepositWithdrawalTransaction> getWithdrawalDepositWalletHistory(long userId, ExchangerCode exchangerCode) {
        ExchangerConnectorService exchangerConnectorService = getExchangerConnectorService(exchangerCode);
        return exchangerConnectorService.getDepositWithdrawalHistory(userId);
    }

    @Override
    public SyncDataResp synchronizeBalanceDataForUser(long userId) {
        List<Balance> balances = balanceService.synchronizeBalances(userId);
        return new SyncDataResp(balances);
    }

    @Override
    public DeleteExchangerResp deleteExchangerForUser(long userId, ExchangerCode code) {
        return new DeleteExchangerResp(userId, balanceService.removeBalance(userId, code).getId());
    }

    private RegisterApiKeysResp generateApiKeysSettingsForUser(User user, String privateKey, String publicApi, ExchangerCode code) {
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

        ApiKeySetting createdApiKeySettings = apiKeySettingRepository.save(apiKeySetting);

        return new RegisterApiKeysResp(user.getId(), createdApiKeySettings.getId(), RegistrationStatus.SUCCESSFUL);
    }

    ExchangerConnectorService getExchangerConnectorService(ExchangerCode exchangerCode) {
        return exchangerServiceFactories.get(exchangerCode).newInstance();
    }
}

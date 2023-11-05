package com.khomishchak.cryptoportfolio.services.exchangers;

import com.khomishchak.cryptoportfolio.model.DepositWithdrawalTransaction;
import com.khomishchak.cryptoportfolio.model.User;
import com.khomishchak.cryptoportfolio.model.enums.ExchangerCode;
import com.khomishchak.cryptoportfolio.model.enums.RegistrationStatus;
import com.khomishchak.cryptoportfolio.model.exchanger.ApiKeySetting;
import com.khomishchak.cryptoportfolio.model.exchanger.ApiKeysPair;
import com.khomishchak.cryptoportfolio.model.exchanger.Balance;
import com.khomishchak.cryptoportfolio.model.requests.RegisterApiKeysReq;
import com.khomishchak.cryptoportfolio.model.requests.RegisterExchangerInfoReq;
import com.khomishchak.cryptoportfolio.model.response.DeleteExchangerResp;
import com.khomishchak.cryptoportfolio.model.response.FirstlyGeneratedBalanceResp;
import com.khomishchak.cryptoportfolio.model.response.RegisterApiKeysResp;
import com.khomishchak.cryptoportfolio.model.response.SyncDataResp;
import com.khomishchak.cryptoportfolio.repositories.ApiKeySettingRepository;
import com.khomishchak.cryptoportfolio.repositories.UserRepository;

import com.khomishchak.cryptoportfolio.services.exchangers.balances.BalancePricingService;
import com.khomishchak.cryptoportfolio.services.exchangers.balances.BalanceService;
import com.khomishchak.cryptoportfolio.services.security.encryption.AesEncryptionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

@Service
public class ExchangerServiceImpl implements ExchangerService {

    private final UserRepository userRepository;
    private final ApiKeySettingRepository apiKeySettingRepository;
    private final AesEncryptionService aesEncryptionService;
    private final BalanceService balanceService;
    private final BalancePricingService balancePricingService;
    private final Map<ExchangerCode, ExchangerConnectorServiceFactory> exchangerServiceFactories;

    public ExchangerServiceImpl(UserRepository userRepository, ApiKeySettingRepository apiKeySettingRepository,
                                AesEncryptionService aesEncryptionService,
                                List<ExchangerConnectorServiceFactory> exchangerServiceFactories,
                                BalanceService balanceService, BalancePricingService balancePricingService) {
        this.userRepository = userRepository;
        this.apiKeySettingRepository = apiKeySettingRepository;
        this.aesEncryptionService = aesEncryptionService;
        this.balanceService = balanceService;
        this.exchangerServiceFactories = exchangerServiceFactories.stream()
                .collect(Collectors.toMap(ExchangerConnectorServiceFactory::getExchangerCode, factory -> factory));
        this.balancePricingService = balancePricingService;
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
        Balance balance = balanceService.getMainBalance(userId, exchangerCode);
        balancePricingService.calculateBalanceValueUpToDate(balance);
        return balance;
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

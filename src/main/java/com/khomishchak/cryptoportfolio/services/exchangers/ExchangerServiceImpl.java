package com.khomishchak.cryptoportfolio.services.exchangers;

import com.khomishchak.cryptoportfolio.model.DepositWithdrawalTransaction;
import com.khomishchak.cryptoportfolio.model.User;
import com.khomishchak.cryptoportfolio.model.enums.ExchangerCode;
import com.khomishchak.cryptoportfolio.model.exchanger.ApiKeySetting;
import com.khomishchak.cryptoportfolio.model.exchanger.ApiKeysPair;
import com.khomishchak.cryptoportfolio.model.exchanger.Balance;
import com.khomishchak.cryptoportfolio.repositories.ApiKeySettingRepository;
import com.khomishchak.cryptoportfolio.repositories.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

@Service
public class ExchangerServiceImpl implements ExchangerService {

    private final UserRepository userRepository;
    private final ApiKeySettingRepository apiKeySettingRepository;
    private final List<ExchangerConnectorServiceFactory> exchangerServiceFactories;

    public ExchangerServiceImpl(UserRepository userRepository, ApiKeySettingRepository apiKeySettingRepository,
            List<ExchangerConnectorServiceFactory> exchangerServiceFactories) {
        this.userRepository             = userRepository;
        this.apiKeySettingRepository    = apiKeySettingRepository;
        this.exchangerServiceFactories  = exchangerServiceFactories;
    }

    @Override
    @Transactional
    public User persistExchangerBalanceForUser(String apiPublicKey, String apiPrivateKey, long userId, ExchangerCode code) {
        User user = userRepository.getReferenceById(userId);

        return generateApiKeysSettingsForUser(user, apiPrivateKey, apiPublicKey, code);
    }

    @Override
    public Balance getMainBalance(long userId, ExchangerCode exchangerCode) {
        ExchangerConnectorService exchangerConnectorService = getExchangerConnectorService(exchangerCode);
        return exchangerConnectorService.getMainBalance(userId);
    }

    @Override
    public List<Balance> getAllMainBalances(long userId) {

        List<ExchangerCode> codes = userRepository.getReferenceById(userId).getApiKeysSettings()
                .stream()
                .map(ApiKeySetting::getCode).toList();

        return exchangerServiceFactories.stream()
                .filter(f -> codes.contains(f.getExchangerCode()))
                .map(f -> f.newInstance().getMainBalance(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<DepositWithdrawalTransaction> getWithdrawalDepositWalletHistory(long userId, ExchangerCode exchangerCode) {
        ExchangerConnectorService exchangerConnectorService = getExchangerConnectorService(exchangerCode);
        return exchangerConnectorService.getDepositWithdrawalHistory(userId);
    }

    private ExchangerConnectorService getExchangerConnectorService(ExchangerCode exchangerCode) {
        ExchangerConnectorServiceFactory factory = exchangerServiceFactories.stream().filter(f -> f.getExchangerCode().equals(exchangerCode))
                .findFirst().orElseThrow(() -> new RuntimeException("no data for the exchanger with code: " + exchangerCode));
        return factory.newInstance();
    }

    private User generateApiKeysSettingsForUser(User user, String privateKey, String publicApi, ExchangerCode code) {
        ApiKeysPair apiKeysPair = ApiKeysPair.builder()
                .publicApi(publicApi)
                .privateKey(privateKey)
                .build();

        ApiKeySetting apiKeySetting = ApiKeySetting.
                builder()
                .user(user)
                .code(code)
                .apiKeys(apiKeysPair)
                .build();

        user.getApiKeysSettings().add(apiKeySetting);

        return userRepository.save(user);
    }
}

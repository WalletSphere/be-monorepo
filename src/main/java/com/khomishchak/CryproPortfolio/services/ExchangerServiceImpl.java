package com.khomishchak.CryproPortfolio.services;

import com.khomishchak.CryproPortfolio.model.User;
import com.khomishchak.CryproPortfolio.model.enums.ExchangerCode;
import com.khomishchak.CryproPortfolio.model.exchanger.ApiKeySetting;
import com.khomishchak.CryproPortfolio.model.exchanger.ApiKeysPair;
import com.khomishchak.CryproPortfolio.model.exchanger.Balance;
import com.khomishchak.CryproPortfolio.repositories.ApiKeySettingRepository;
import com.khomishchak.CryproPortfolio.repositories.UserRepository;
import com.khomishchak.CryproPortfolio.services.exchangers.ExchangerConnectorService;
import com.khomishchak.CryproPortfolio.services.exchangers.ExchangerConnectorServiceFactory;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

@Service
public class ExchangerServiceImpl implements ExchangersService {

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
    public User persistExchangerBalanceForUser(String apiPublicKey, String apiPrivateKey, long accoId, ExchangerCode code) {
        User user = userRepository.getReferenceById(accoId);

        return generateApiKeysSettingsForUser(user, apiPrivateKey, apiPublicKey, code);
    }

    @Override
    public Balance getMainBalance(long userId, ExchangerCode code) {
        ExchangerConnectorServiceFactory factory = exchangerServiceFactories.stream().filter(f -> f.getExchangerCode().equals(code))
                .findFirst().orElseThrow(() -> new RuntimeException("no data for the exchanger with code: " + code));
        ExchangerConnectorService exchangerConnectorService = factory.newInstance();
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

    @Transactional
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

        apiKeySettingRepository.save(apiKeySetting);

        user.getApiKeysSettings().add(apiKeySetting);

        return userRepository.save(user);
    }
}

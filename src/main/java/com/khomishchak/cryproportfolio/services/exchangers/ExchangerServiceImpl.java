package com.khomishchak.cryproportfolio.services.exchangers;

import com.khomishchak.cryproportfolio.model.DepositWithdrawalTransaction;
import com.khomishchak.cryproportfolio.model.User;
import com.khomishchak.cryproportfolio.model.enums.ExchangerCode;
import com.khomishchak.cryproportfolio.model.exchanger.ApiKeySetting;
import com.khomishchak.cryproportfolio.model.exchanger.ApiKeysPair;
import com.khomishchak.cryproportfolio.model.exchanger.Balance;
import com.khomishchak.cryproportfolio.repositories.ApiKeySettingRepository;
import com.khomishchak.cryproportfolio.repositories.UserRepository;

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
    public User persistExchangerBalanceForUser(String apiPublicKey, String apiPrivateKey, long accoId, ExchangerCode code) {
        User user = userRepository.getReferenceById(accoId);

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
    public List<DepositWithdrawalTransaction> getWithdrawalDepositWalletHistory(long accoId, ExchangerCode exchangerCode) {
        ExchangerConnectorService exchangerConnectorService = getExchangerConnectorService(exchangerCode);
        return exchangerConnectorService.getDepositWithdrawalHistory(accoId);
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

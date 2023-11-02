package com.khomishchak.cryptoportfolio.adapters;

import com.khomishchak.cryptoportfolio.model.exchanger.ApiKeySetting;
import com.khomishchak.cryptoportfolio.model.exchanger.ApiKeysPair;
import com.khomishchak.cryptoportfolio.model.exchanger.DecryptedApiKeySettingDTO;
import com.khomishchak.cryptoportfolio.repositories.ApiKeySettingRepository;
import com.khomishchak.cryptoportfolio.services.security.encryption.AesEncryptionService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApiKeySettingRepositoryAdapter {

    private final ApiKeySettingRepository apiKeySettingRepository;
    private final AesEncryptionService aesEncryptionService;

    public ApiKeySettingRepositoryAdapter(ApiKeySettingRepository apiKeySettingRepository, AesEncryptionService aesEncryptionService) {
        this.apiKeySettingRepository = apiKeySettingRepository;
        this.aesEncryptionService    = aesEncryptionService;
    }

    public List<DecryptedApiKeySettingDTO> findAllByUserId(long userId) {
         return apiKeySettingRepository.findAllByUserId(userId).stream()
                .map(settings -> {
                    ApiKeysPair originalKeysPair = settings.getApiKeys();
                    String decryptedPrivateKey = aesEncryptionService.decrypt(originalKeysPair.getPrivateKey());
                    String decryptedPublicApi = aesEncryptionService.decrypt(originalKeysPair.getPublicApi());
                    return DecryptedApiKeySettingDTO.builder()
                            .publicKey(decryptedPublicApi)
                            .privateKey(decryptedPrivateKey)
                            .code(settings.getCode())
                            .build();
                }).toList();
    }
}

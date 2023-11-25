package com.khomishchak.ws.adapters;

import com.khomishchak.ws.model.exchanger.ApiKeysPair;
import com.khomishchak.ws.model.exchanger.DecryptedApiKeySettingDTO;
import com.khomishchak.ws.repositories.ApiKeySettingRepository;
import com.khomishchak.ws.services.security.encryption.AesEncryptionService;
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

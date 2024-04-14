package com.walletsphere.wsmonolith.services.exchangers.apikeys;

import com.walletsphere.wsmonolith.model.enums.ExchangerCode;
import com.walletsphere.wsmonolith.model.exchanger.ApiKeysPair;
import com.walletsphere.wsmonolith.model.exchanger.DecryptedApiKeySettingDTO;
import com.walletsphere.wsmonolith.repositories.ApiKeySettingRepository;
import com.walletsphere.wsmonolith.services.security.encryption.AesEncryptionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiKeySettingServiceImpl implements ApiKeySettingService {

    private final ApiKeySettingRepository apiKeySettingRepository;
    private final AesEncryptionService aesEncryptionService;

    public ApiKeySettingServiceImpl(ApiKeySettingRepository apiKeySettingRepository, AesEncryptionService aesEncryptionService) {
        this.apiKeySettingRepository = apiKeySettingRepository;
        this.aesEncryptionService    = aesEncryptionService;
    }

    @Override
    public List<DecryptedApiKeySettingDTO> getDecryptApiKeySettings(long userId) {
        return apiKeySettingRepository.findAllByUserId(userId).stream()
                .map(settings -> {
                    ApiKeysPair encryptedKeysPair = settings.getApiKeys();
                    return buildDecryptedApiKeySettingDTO(encryptedKeysPair, settings.getCode());
                }).toList();
    }

    private DecryptedApiKeySettingDTO buildDecryptedApiKeySettingDTO(ApiKeysPair encryptedKeysPair, ExchangerCode code) {
        String decryptedPrivateKey = decryptKey(encryptedKeysPair.getPrivateKey());
        String decryptedPublicApi = decryptKey(encryptedKeysPair.getPublicApi());
        return DecryptedApiKeySettingDTO.builder()
                .publicKey(decryptedPublicApi)
                .privateKey(decryptedPrivateKey)
                .code(code)
                .build();
    }

    private String decryptKey(String encryptedKey) {
        return aesEncryptionService.decrypt(encryptedKey);
    }
}

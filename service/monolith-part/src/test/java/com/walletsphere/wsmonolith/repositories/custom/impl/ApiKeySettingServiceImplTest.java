package com.walletsphere.wsmonolith.repositories.custom.impl;

import com.walletsphere.wsmonolith.model.User;
import com.walletsphere.wsmonolith.model.enums.ExchangerCode;
import com.walletsphere.wsmonolith.model.exchanger.ApiKeySetting;
import com.walletsphere.wsmonolith.model.exchanger.ApiKeysPair;
import com.walletsphere.wsmonolith.model.exchanger.DecryptedApiKeySettingDTO;
import com.walletsphere.wsmonolith.repositories.ApiKeySettingRepository;
import com.walletsphere.wsmonolith.services.exchangers.apikeys.ApiKeySettingService;
import com.walletsphere.wsmonolith.services.exchangers.apikeys.ApiKeySettingServiceImpl;
import com.walletsphere.wsmonolith.services.security.encryption.AesEncryptionService;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ApiKeySettingServiceImplTest {

    private static final long USER_ID = 1L;
    private static final String ENCRYPTED_PUB_KEY = "encryptedPubKey";
    private static final String DECRYPTED_PUB_KEY = "decryptedPubKey";
    private static final String ENCRYPTED_PRI_KEY = "encryptedPriKey";
    private static final String DECRYPTED_PRI_KEY = "decryptedPriKey";

    @Mock
    private ApiKeySettingRepository apiKeySettingRepository;
    @Mock
    private AesEncryptionService aesEncryptionService;

    private ApiKeySettingService apiKeySettingService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(USER_ID)
                .build();
        apiKeySettingService = new ApiKeySettingServiceImpl(apiKeySettingRepository, aesEncryptionService);
    }

    @Test
    void shouldReturnDecryptedKeysByUserId() {
        // given
        ApiKeysPair encryptedApiKeysPair = new ApiKeysPair(ENCRYPTED_PUB_KEY, ENCRYPTED_PRI_KEY);
        ApiKeySetting encryptedApiKeySetting = ApiKeySetting.builder()
                .user(testUser)
                .code(ExchangerCode.WHITE_BIT)
                .apiKeys(encryptedApiKeysPair)
                .build();

        when(apiKeySettingRepository.findAllByUserId(eq(USER_ID))).thenReturn(List.of(encryptedApiKeySetting));
        when(aesEncryptionService.decrypt(eq(ENCRYPTED_PUB_KEY))).thenReturn(DECRYPTED_PUB_KEY);
        when(aesEncryptionService.decrypt(eq(ENCRYPTED_PRI_KEY))).thenReturn(DECRYPTED_PRI_KEY);

        // when
        List<DecryptedApiKeySettingDTO> result = apiKeySettingService.getDecryptApiKeySettings(USER_ID);

        // then
        assertThat(result.size()).isEqualTo(1);

        DecryptedApiKeySettingDTO resultEntity = result.get(0);
        assertThat(resultEntity.getPublicKey()).isEqualTo(DECRYPTED_PUB_KEY);
        assertThat(resultEntity.getPrivateKey()).isEqualTo(DECRYPTED_PRI_KEY);
    }
}
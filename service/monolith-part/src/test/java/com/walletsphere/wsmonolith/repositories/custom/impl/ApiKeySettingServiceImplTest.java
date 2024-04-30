package com.walletsphere.wsmonolith.repositories.custom.impl;

import static com.walletsphere.wsmonolith.model.enums.ExchangerCode.WHITE_BIT;
import com.walletsphere.wsmonolith.model.User;
import com.walletsphere.wsmonolith.model.exchanger.ApiKeySetting;
import com.walletsphere.wsmonolith.model.exchanger.ApiKeysPair;
import com.walletsphere.wsmonolith.model.exchanger.Balance;
import com.walletsphere.wsmonolith.model.exchanger.DecryptedApiKeySettingDTO;
import com.walletsphere.wsmonolith.model.requests.RegisterApiKeysReq;
import com.walletsphere.wsmonolith.repositories.ApiKeySettingRepository;
import com.walletsphere.wsmonolith.services.exchangers.apikeys.ApiKeySettingService;
import com.walletsphere.wsmonolith.services.exchangers.apikeys.ApiKeySettingServiceImpl;
import com.walletsphere.wsmonolith.services.security.encryption.AesEncryptionService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ApiKeySettingServiceImplTest {

    private static final long USER_ID = 1L;
    private static final long BALANCE_ID = 2L;
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
    private Balance testBalance;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(USER_ID).build();
        testBalance = Balance.builder().id(BALANCE_ID).build();
        apiKeySettingService = new ApiKeySettingServiceImpl(apiKeySettingRepository, aesEncryptionService);
    }

    @Test
    void shouldReturnDecryptedKeysByUserId() {
        // given
        ApiKeysPair encryptedApiKeysPair = new ApiKeysPair(ENCRYPTED_PUB_KEY, ENCRYPTED_PRI_KEY);
        ApiKeySetting encryptedApiKeySetting = ApiKeySetting.builder()
                .user(testUser)
                .code(WHITE_BIT)
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

    @Test
    void saveApiKeysSettings_shouldReturnSavedApiKeySettings() {
        // given
        testUser.setApiKeysSettings(new ArrayList<>());

        ApiKeysPair encryptedApiKeysPair = new ApiKeysPair(ENCRYPTED_PUB_KEY, ENCRYPTED_PRI_KEY);
        ApiKeySetting encryptedApiKeySetting = ApiKeySetting.builder()
                .user(testUser)
                .code(WHITE_BIT)
                .balance(testBalance)
                .apiKeys(encryptedApiKeysPair)
                .build();

        RegisterApiKeysReq registerApiKeysReq = new RegisterApiKeysReq(DECRYPTED_PUB_KEY, DECRYPTED_PRI_KEY, WHITE_BIT);

        ApiKeySetting expected = new ApiKeySetting(1L, testUser, testBalance, WHITE_BIT, encryptedApiKeysPair);

        when(apiKeySettingRepository.save(eq(encryptedApiKeySetting))).thenReturn(expected);
        when(aesEncryptionService.encrypt(eq(DECRYPTED_PUB_KEY))).thenReturn(ENCRYPTED_PUB_KEY);
        when(aesEncryptionService.encrypt(eq(DECRYPTED_PRI_KEY))).thenReturn(ENCRYPTED_PRI_KEY);


        // when
        ApiKeySetting result = apiKeySettingService.saveApiKeysSettings(testUser, testBalance, registerApiKeysReq);

        // then

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getApiKeys()).isEqualTo(encryptedApiKeysPair);
        assertThat(result.getCode()).isEqualTo(WHITE_BIT);
        assertThat(result.getUser()).isEqualTo(testUser);
        assertThat(result.getBalance().getId()).isEqualTo(BALANCE_ID);
    }
}
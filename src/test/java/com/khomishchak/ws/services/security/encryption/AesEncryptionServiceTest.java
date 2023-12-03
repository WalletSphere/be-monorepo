package com.khomishchak.ws.services.security.encryption;

import com.khomishchak.ws.model.enums.EncryptionAlgorithm;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class AesEncryptionServiceTest {

    private SecretKey secretKey;

    private AesEncryptionService aesEncryptionService;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        secretKey = getAesSecretKey();
        aesEncryptionService = new AesEncryptionService(secretKey);
    }

    @Test
    void givenString_whenEncryptAndDecrypt_thenOriginalString() {
        String originalString = "Test String";
        String encrypted = aesEncryptionService.encrypt(originalString);
        String decrypted = aesEncryptionService.decrypt(encrypted);

        assertThat(encrypted).isNotNull();
        assertThat(originalString).isNotEqualTo(encrypted);
        assertThat(originalString).isEqualTo(decrypted);
    }

    @Test
    void whenEncryptWithInvalidData_thenThrowException() {
        assertThrows(UnsupportedOperationException.class, () -> {
            aesEncryptionService.encrypt(null);
        });
    }

    @Test
    void whenDecryptWithInvalidData_thenThrowException() {
        assertThrows(UnsupportedOperationException.class, () -> {
            aesEncryptionService.decrypt(null);
        });
    }

    private SecretKey getAesSecretKey() throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        final byte[] hashbytes = digest.digest(
                "mockSecretKeyValue".getBytes(StandardCharsets.UTF_8));

        return new SecretKeySpec(hashbytes, EncryptionAlgorithm.AES.name());
    }
}
package com.khomishchak.cryptoportfolio.services.security.encryption;

import com.khomishchak.cryptoportfolio.model.enums.EncryptionAlgorithm;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

// TODO: add interface responsible for encryption, ex: EncryptionService
@Service
public class AesEncryptionService {

    private final SecretKey secretKey;

    public AesEncryptionService(@Qualifier("ws.encryption.aes.secret-key") SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    public String encrypt(String data) {
        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new UnsupportedOperationException(String.format("Couldn't encrypt data: %s \nErrorMessage: %s", data, e.getMessage()));
        }
    }

    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new UnsupportedOperationException(String.format("Couldn't decrypt data: %s \nErrorMessage: %s", encryptedData, e.getMessage()));
        }
    }

    private Cipher getCipher(int mode) throws Exception {
        Cipher cipher = Cipher.getInstance(EncryptionAlgorithm.AES.name());
        cipher.init(mode, secretKey);
        return cipher;
    }
}

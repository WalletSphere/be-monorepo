package com.khomishchak.cryptoportfolio.configurations;

import com.khomishchak.cryptoportfolio.model.enums.EncryptionAlgorithm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Configuration
@PropertySource(value = "classpath:secret.yaml", ignoreResourceNotFound = true)
public class EncryptionConfig {

    @Bean
    @Qualifier("ws.encryption.aes.secret-key")
    public SecretKey getAesSecretKey(@Value("${ws.security.secret-key:mock}") String secretKeyValue) throws NoSuchAlgorithmException {

        final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        final byte[] hashbytes = digest.digest(
                secretKeyValue.getBytes(StandardCharsets.UTF_8));

        return new SecretKeySpec(hashbytes, EncryptionAlgorithm.AES.name());
    }
}

package com.khomishchak.authservice.service.util;

import com.khomishchak.authservice.model.auth.DeviceType;
import jakarta.annotation.PostConstruct;
import org.jose4j.jwk.EcJwkGenerator;
import org.jose4j.jwk.EllipticCurveJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.keys.EllipticCurves;
import org.jose4j.lang.JoseException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;

@Component
public class JwtUtil {
    private Long jwtWebExpirationTimeInMinutes;
    private Long jwtAppExpirationTimeInMinutes;

    private static final String ALG_HEADER = AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256;

    private Key privateKey;
    private Key publicKey;

    @Value("${ws.security.jwt.expiration-time.web.minutes:30}")
    public void setJwtWebExpirationTimeInMinutes(Long jwtWebExpirationTimeInMinutes) {
        this.jwtWebExpirationTimeInMinutes = jwtWebExpirationTimeInMinutes;
    }

    @Value("${ws.security.jwt.expiration-time.app.minutes:4320}")
    public void setJwtAppExpirationTimeInMinutes(Long jwtAppExpirationTimeInMinutes) {
        this.jwtAppExpirationTimeInMinutes = jwtAppExpirationTimeInMinutes;
    }

    @PostConstruct
    public void initKeys() {
        EllipticCurveJsonWebKey ecKey;
        try {
            ecKey = EcJwkGenerator.generateJwk(EllipticCurves.P256);
            this.privateKey = ecKey.getPrivateKey();
            this.publicKey = ecKey.getECPublicKey();
        } catch (JoseException e) {
            throw new RuntimeException("Error generating keys", e);
        }
    }

    public String generateToken(Long userId, DeviceType deviceType) {
        JwtClaims jwtClaims = createJwtClaims(userId, deviceType);
        JsonWebSignature jsonWebSignature = createJsonWebSignature(jwtClaims);
        return getTokenFromJsonWebSignature(jsonWebSignature);
    }

    private JwtClaims createJwtClaims (Long userId, DeviceType deviceType) {
        JwtClaims jwtClaims = new JwtClaims();
        jwtClaims.setSubject(String.valueOf(userId));
        jwtClaims.setIssuedAtToNow();
        jwtClaims.setExpirationTimeMinutesInTheFuture(deviceType.equals(DeviceType.WEB) ? jwtWebExpirationTimeInMinutes
                : jwtAppExpirationTimeInMinutes);
        return jwtClaims;
    }

    private JsonWebSignature createJsonWebSignature(JwtClaims jwtClaims) {
        JsonWebSignature jws = new JsonWebSignature();
        jws.setAlgorithmHeaderValue(ALG_HEADER);
        jws.setKey(privateKey);
        jws.setPayload(jwtClaims.toJson());
        return jws;
    }

    private String getTokenFromJsonWebSignature(JsonWebSignature signature) {
        try {
            return signature.getCompactSerialization();
        } catch (JoseException e) {
            throw new RuntimeException("Error generating JWT", e);
        }
    }

    public boolean isTokenExpired(String token) {
        boolean tokenIsValid;
        try {
            tokenIsValid = extractAllClaims(token).getExpirationTime().isAfter(NumericDate.now());
        } catch (MalformedClaimException exception) {
            tokenIsValid = false;
            // TODO: log error
        }
        return tokenIsValid;
    }

    public Long extractUserId(String token) {
        long userId;

        try {
            userId = Long.parseLong(extractAllClaims(token).getSubject());
        } catch (MalformedClaimException | RuntimeException exception) {
            return null;
        }

        return userId;
    }

    private JwtClaims extractAllClaims(String token) {
        return getJwtContext(createJwtConsumer(), token).getJwtClaims();
    }

    private JwtConsumer createJwtConsumer() {
        return new JwtConsumerBuilder()
                .setRequireIssuedAt()
                .setRequireExpirationTime()
                .setVerificationKey(publicKey)
                .build();
    }

    private JwtContext getJwtContext(JwtConsumer jwtConsumer, String token) {
        try{
            return jwtConsumer.process(token);
        } catch (InvalidJwtException e) {
            throw new RuntimeException("Was not Able to process jwt token: %s", e);
        }
    }
}

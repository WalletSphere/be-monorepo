package com.khomishchak.cryproportfolio.services.security;

import org.jose4j.jwk.EcJwkGenerator;
import org.jose4j.jwk.EllipticCurveJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.keys.EllipticCurves;
import org.jose4j.lang.JoseException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Map;

import jakarta.annotation.PostConstruct;

@Service
public class JwtServiceImpl implements JwtService {

    private static final Long EXPIRATION_TIME_IN_MINUTES = 30L;
    private static final String ALG_HEADER = AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256;

    private Key privateKey;
    private Key publicKey;

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

    @Override
    public String generateToken(Map<String, String> extraClaims, UserDetails userDetails) {
        JwtClaims jwtClaims = new JwtClaims();
        jwtClaims.setSubject(userDetails.getUsername());
        jwtClaims.setIssuedAtToNow();
        jwtClaims.setExpirationTimeMinutesInTheFuture(EXPIRATION_TIME_IN_MINUTES);

        JsonWebSignature jws = new JsonWebSignature();
        jws.setAlgorithmHeaderValue(ALG_HEADER);
        jws.setKey(privateKey);
        jws.setPayload(jwtClaims.toJson());

        try {
            return jws.getCompactSerialization();
        } catch (JoseException e) {
            throw new RuntimeException("Error generating JWT", e);
        }
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {

        return isTokenExpired(token) && userDetails.getUsername().equals(extractUsername(token));
    }

    private boolean isTokenExpired(String token) {
        boolean tokenIsValid;
        try {
            tokenIsValid = extractAllClaims(token).getExpirationTime().isAfter(NumericDate.now());
        } catch (MalformedClaimException exception) {
            tokenIsValid = false;
            // logging
        }
        return tokenIsValid;
    }

    @Override
    public String extractUsername(String token) {
        String username;

        try {
            username = extractAllClaims(token).getSubject();
        } catch (MalformedClaimException exception) {
            username = null;
            // logging
        }

        return username;
    }

    private JwtClaims extractAllClaims(String token) {

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireIssuedAt()
                .setRequireExpirationTime()
                .setVerificationKey(publicKey)
                .build();

        JwtContext jwtContext;

        try{
            jwtContext = jwtConsumer.process(token);
        } catch (InvalidJwtException e) {
            throw new RuntimeException("Was not Able to process jwt token: %s", e);
        }

        return jwtContext.getJwtClaims();
    }
}

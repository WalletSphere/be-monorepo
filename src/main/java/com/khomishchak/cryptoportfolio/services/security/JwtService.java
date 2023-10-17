package com.khomishchak.cryptoportfolio.services.security;

import com.khomishchak.cryptoportfolio.model.enums.DeviceType;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

public interface JwtService {

    String generateToken(Map<String, String> extraClaims, UserDetails userDetails, DeviceType registrationDeviceType);

    boolean isTokenValid(String token, UserDetails userDetails);

    String extractUsername(String token);

    Optional<String> getToken(HttpServletRequest request);

    Long extractUserId(String token);
}

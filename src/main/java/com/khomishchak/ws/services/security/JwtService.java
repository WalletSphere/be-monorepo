package com.khomishchak.ws.services.security;

import com.khomishchak.ws.model.enums.DeviceType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.Optional;

public interface JwtService {

    String generateToken(Map<String, String> extraClaims, UserDetails userDetails, DeviceType registrationDeviceType);

    boolean isTokenValid(String token, UserDetails userDetails);

    String extractUsername(String token);

    Optional<String> getToken(HttpServletRequest request);

    Long extractUserId(String token);
}

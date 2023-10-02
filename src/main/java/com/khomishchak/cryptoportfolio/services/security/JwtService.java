package com.khomishchak.cryptoportfolio.services.security;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtService {

    String generateToken(Map<String, String> extraClaims, UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);

    String extractUsername(String token);
}

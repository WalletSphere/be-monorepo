package com.khomishchak.apigateway.service;

import com.khomishchak.apigateway.model.ProcessedTokenResp;

public interface JwtService {
    ProcessedTokenResp processToken(String token);
}

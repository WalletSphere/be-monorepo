package com.walletsphere.apigateway.service;

import com.walletsphere.model.authentication.ProcessedJwtTokenResp;

public interface JwtService {
    ProcessedJwtTokenResp processToken(String token);
}

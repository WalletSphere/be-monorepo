package com.khomishchak.apigateway.service;

import com.walletsphere.model.authentication.ProcessedJwtTokenResp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${ws.authentication.token.validation.url:http://localhost:8088/api/v1/auth/token}")
    private String validateTokenUrl;
    private final RestTemplate restTemplate;

    public JwtServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ProcessedJwtTokenResp processToken(String token) {
        if (token == null) throw new RuntimeException("authetication token is null");
        HttpEntity<String> requestEntity = createAuthorizationRequest(Map.of(HttpHeaders.AUTHORIZATION, token));
        return restTemplate.postForObject(validateTokenUrl, requestEntity, ProcessedJwtTokenResp.class);
    }

    private HttpEntity<String> createAuthorizationRequest(Map<String, String> headerParams) {
        HttpHeaders headers = createHttpRequestHeaders(headerParams);
        return new HttpEntity<>(headers);
    }

    private HttpHeaders createHttpRequestHeaders(Map<String, String> headerParams) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headerParams.forEach(headers::set);
        return headers;
    }
}

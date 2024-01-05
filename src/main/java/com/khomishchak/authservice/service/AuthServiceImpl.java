package com.khomishchak.authservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khomishchak.authservice.exception.AuthException;
import com.khomishchak.authservice.model.auth.dto.AuthenticationRequestDTO;
import com.khomishchak.authservice.model.auth.dto.CreateUserRequestDTO;
import com.khomishchak.authservice.model.auth.request.LoginReq;
import com.khomishchak.authservice.model.auth.request.RegistrationReq;
import com.khomishchak.authservice.model.auth.resp.ErrorResp;
import com.khomishchak.authservice.model.auth.resp.LoginResp;
import com.khomishchak.authservice.model.auth.resp.ProcessedTokenResp;
import com.khomishchak.authservice.model.auth.resp.RegistrationResp;
import com.khomishchak.authservice.service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthServiceImpl implements AuthService {

    private String createUserUrl;
    private String authenticateUserUrl;

    @Value("${ws.users.authenticate.url:http://localhost:8080/users}")
    public void setCreateUserUrl(String createUserUrl) {
        this.createUserUrl = createUserUrl;
    }

    @Value("${ws.users.authenticate.url:http://localhost:8080/users/authenticate}")
    public void setAuthenticateUserUrl(String authenticateUserUrl) {
        this.authenticateUserUrl = authenticateUserUrl;
    }

    private static final String TOKEN_PREFIX = "Bearer ";
    private final ObjectMapper mapper;
    private RestTemplate restTemplate;
    private JwtUtil jwtUtil;

    public AuthServiceImpl(@Qualifier("wsAuthServiceRestTemplate") RestTemplate restTemplate, JwtUtil jwtUtil,
                           @Qualifier("wsAuthServiceObjectMapper") ObjectMapper mapper) {
        this.restTemplate = restTemplate;
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
    }

    @Override
    public RegistrationResp register(RegistrationReq request) {
        CreateUserRequestDTO createUserRequest = new CreateUserRequestDTO(request.username(), request.password(),
                request.email(), request.acceptTC());
        long userId = makePostRequest(createUserUrl, createUserRequest, Long.class);
        return new RegistrationResp(userId, jwtUtil.generateToken(userId, request.deviceType()));
    }

    @Override
    public LoginResp login(LoginReq request) {
        AuthenticationRequestDTO authenticationRequest = new AuthenticationRequestDTO(request.username(), request.password());
        long userId = makePostRequest(authenticateUserUrl, authenticationRequest, Long.class);
        return new LoginResp(userId, jwtUtil.generateToken(userId, request.deviceType()));
    }

    private <T> T makePostRequest(String url, Object request, Class<T> responseType) {
        try {
            return restTemplate.postForObject(url, request, responseType);
        } catch (HttpClientErrorException clientErrorException) {
            handleErrorResponse(clientErrorException);
            return null;
        }
    }

    @Override
    public ProcessedTokenResp validateToken(String tokenHeader) {
        String token = tokenHeader.substring(TOKEN_PREFIX.length());
        return new ProcessedTokenResp(jwtUtil.extractUserId(token), jwtUtil.isTokenExpired(token));
    }

    private void handleErrorResponse(HttpStatusCodeException exception) {
        throw new AuthException("Could not authorize user", mapErrorResponseMessage(exception.getMessage()));
    }

    private ErrorResp mapErrorResponseMessage(String errorMessageJson) {
        String jsonResponse = errorMessageJson.substring(errorMessageJson.indexOf('{'));
        try {
            return mapper.readValue(jsonResponse, ErrorResp.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

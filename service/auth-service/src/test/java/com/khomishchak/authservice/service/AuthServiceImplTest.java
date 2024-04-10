package com.khomishchak.authservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khomishchak.authservice.exception.CouldNotAuthenticateUserException;
import com.khomishchak.authservice.model.auth.DeviceType;
import com.khomishchak.authservice.model.auth.dto.AuthenticationRequestDTO;
import com.khomishchak.authservice.model.auth.dto.CreateUserRequestDTO;
import com.khomishchak.authservice.model.auth.request.LoginReq;
import com.khomishchak.authservice.model.auth.request.RegistrationReq;
import com.khomishchak.authservice.model.auth.resp.ErrorResp;
import com.khomishchak.authservice.model.auth.resp.LoginResp;
import com.khomishchak.authservice.model.auth.resp.ProcessedTokenResp;
import com.khomishchak.authservice.model.auth.resp.RegistrationResp;
import com.khomishchak.authservice.service.util.JwtUtil;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private static final String CREATE_USER_URL = "createUserUrl";
    private static final String AUTHENTICATE_USER_URL = "authenticateUserUrl";
    private static final Long USER_ID = 1L;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String JWT_TOKEN = "jwtToken";

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private JwtUtil jwtUtil;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(restTemplate, jwtUtil, mapper);
        authService.setCreateUserUrl(CREATE_USER_URL);
        authService.setAuthenticateUserUrl(AUTHENTICATE_USER_URL);
    }

    @Test
    void shouldReturnPositiveRegistrationResp_whenRegistratedUser() {
        // given
        DeviceType deviceType = DeviceType.WEB;
        RegistrationReq request = new RegistrationReq(USERNAME, PASSWORD, EMAIL, true, deviceType);
        CreateUserRequestDTO createUserRequest = new CreateUserRequestDTO(USERNAME, PASSWORD, EMAIL, true);

        when(restTemplate.postForObject(eq(CREATE_USER_URL), eq(createUserRequest), eq(Long.class))).thenReturn(USER_ID);
        when(jwtUtil.generateToken(eq(USER_ID), eq(deviceType))).thenReturn(JWT_TOKEN);
        // when
        RegistrationResp result = authService.register(request);

        // then
        assertEquals(USER_ID, result.userId());
        assertEquals(JWT_TOKEN, result.jwt());
    }

    @Test
    void shouldReturnPositiveLoginResp_whenAuthorizedUser() {
        // given
        DeviceType deviceType = DeviceType.WEB;
        LoginReq request = new LoginReq(USERNAME, PASSWORD, deviceType);
        AuthenticationRequestDTO authenticationRequest = new AuthenticationRequestDTO(USERNAME, PASSWORD);

        when(restTemplate.postForObject(eq(AUTHENTICATE_USER_URL), eq(authenticationRequest), eq(Long.class))).thenReturn(USER_ID);
        when(jwtUtil.generateToken(eq(USER_ID), eq(deviceType))).thenReturn(JWT_TOKEN);
        // when
        LoginResp result = authService.login(request);

        // then
        assertEquals(USER_ID, result.userId());
        assertEquals(JWT_TOKEN, result.jwt());
    }

    @Test
    void shouldThrowAuthException_whenAuthorizationFailed() throws JsonProcessingException {
        // given
        DeviceType deviceType = DeviceType.WEB;
        LoginReq request = new LoginReq(USERNAME, PASSWORD, deviceType);
        AuthenticationRequestDTO authenticationRequest = new AuthenticationRequestDTO(USERNAME, PASSWORD);

        ErrorResp errorResp = new ErrorResp("type", "message", new ArrayList<>());
        when(restTemplate.postForObject(eq(AUTHENTICATE_USER_URL), eq(authenticationRequest), eq(Long.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN, "{errorMessage}"));
        when(mapper.readValue(eq("{errorMessage}"), eq(ErrorResp.class)))
                .thenReturn(errorResp);
        // when
        CouldNotAuthenticateUserException result = assertThrows(CouldNotAuthenticateUserException.class, () -> {
            authService.login(request);
        });

        // then
        assertEquals(errorResp, result.getErrorResponse());
    }

    @Test
    void shouldShouldReturnValidatedTokenResponse() {
        // given
        String tokenHeader = "Bearer token";
        String token = "token";

        when(jwtUtil.extractUserId(eq(token))).thenReturn(USER_ID);
        when(jwtUtil.isTokenExpired(eq(token))).thenReturn(true);
        // when
        ProcessedTokenResp processedTokenResp = authService.validateToken(tokenHeader);

        // then
        assertEquals(true, processedTokenResp.validated());
        assertEquals(USER_ID, processedTokenResp.userId());
    }
}
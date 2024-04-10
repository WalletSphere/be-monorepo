package com.khomishchak.authservice.service.util;

import com.khomishchak.authservice.model.auth.DeviceType;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtUtilTest {
    private static final long USER_ID = 1L;

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        jwtUtil.initKeys();
        jwtUtil.setJwtWebExpirationTimeInMinutes(30L);
        jwtUtil.setJwtAppExpirationTimeInMinutes(4320L);
    }

    @Test
    void shouldGenerateToken_whenUsernameAndIdArePresent() {
        // given

        // when
        String token = jwtUtil.generateToken(USER_ID, DeviceType.WEB);

        // then
        assertNotNull(token);
    }

    @Test
    void whenTokenIsValid_thenCorrectUserIdIsExtracted() {
        // given

        // when
        String token = jwtUtil.generateToken(USER_ID, DeviceType.WEB);
        Long userId = jwtUtil.extractUserId(token);

        // then
        assertEquals(USER_ID, userId);
    }

    @Test
    void shouldReturnTrue_whenTokenIsNotExpired() {
        // given
        String token = jwtUtil.generateToken(USER_ID, DeviceType.WEB);

        // when
        boolean result = jwtUtil.isTokenExpired(token);

        // then
        assertEquals(true, result);
    }
}
package com.khomishchak.ws.services.security;

import com.khomishchak.ws.model.User;
import com.khomishchak.ws.model.enums.DeviceType;
import com.khomishchak.ws.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    private static final long USER_ID = 1L;
    private static final String USERNAME = "Username";
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private JwtServiceImpl jwtService;

    private UserDetailsImpl userDetails;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        userDetails = new UserDetailsImpl(testUser);
        jwtService.initKeys();
        jwtService.setJwtWebExpirationTimeInMinutes(30L);
        jwtService.setJwtAppExpirationTimeInMinutes(4320L);
    }

    @Test
    void shouldGenerateToken_whenUsernameAndIdArePresent() {
        // given
        testUser.setId(USER_ID);
        testUser.setUsername(USERNAME);
        Map<String, String> extraClaims = new HashMap<>();

        // when
        String token = jwtService.generateToken(extraClaims, userDetails, DeviceType.WEB);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void shouldThrowIllegalArgumentException_whenUsernameAndIdAreNotPresent() {
        // given
        Map<String, String> extraClaims = new HashMap<>();

        // then
        assertThrows(IllegalArgumentException.class, () -> {
            jwtService.generateToken(extraClaims, userDetails, DeviceType.WEB);
        });
    }

    @Test
    void whenTokenIsValid_thenCorrectUsernameIsExtracted() {
        // given
        testUser.setId(USER_ID);
        testUser.setUsername(USERNAME);
        Map<String, String> extraClaims = new HashMap<>();

        // when
        String token = jwtService.generateToken(extraClaims, userDetails, DeviceType.WEB);
        String extractedUsername = jwtService.extractUsername(token);

        // then
        assertThat(extractedUsername).isEqualTo(USERNAME);
    }

    @Test
    void shouldReturnTrue_whenTokenIsValid() {
        // given
        testUser.setId(USER_ID);
        testUser.setUsername(USERNAME);

        String token = jwtService.generateToken(Collections.EMPTY_MAP, userDetails, DeviceType.WEB);

        // when
        boolean result = jwtService.isTokenValid(token, userDetails);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalse_whenUsernameIsNotValid() {
        // given
        testUser.setId(USER_ID);
        testUser.setUsername(USERNAME);

        String token = jwtService.generateToken(Collections.EMPTY_MAP, userDetails, DeviceType.WEB);

        // when
        testUser.setUsername("wrongUsername");
        boolean result = jwtService.isTokenValid(token, userDetails);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnNonEmptyOptional_whenGetTokenFromRequest() {
        // given
        String token = "someToken";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);

        // when
        Optional<String> result = jwtService.getToken(request);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(token);
    }
}
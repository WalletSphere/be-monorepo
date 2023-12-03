package com.khomishchak.ws.services;

import com.khomishchak.ws.exceptions.UserNotFoundException;
import com.khomishchak.ws.model.User;
import com.khomishchak.ws.model.enums.DeviceType;
import com.khomishchak.ws.model.enums.UserRole;
import com.khomishchak.ws.model.requests.LoginRequest;
import com.khomishchak.ws.model.requests.RegistrationRequest;
import com.khomishchak.ws.model.response.LoginResult;
import com.khomishchak.ws.model.response.RegistrationResult;
import com.khomishchak.ws.repositories.UserRepository;
import com.khomishchak.ws.security.UserDetailsImpl;
import com.khomishchak.ws.services.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final long USER_ID = 1L;
    private static final String USER_USERNAME = "testUsername";
    private static final String USER_PASSWORD = "testPassword";
    private static final String USER_EMAIL = "testEmail@gmail.com";

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;

    private UserServiceImpl userService;

    User testUser;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, authenticationManager, jwtService);
    }

    @Test
    void shouldReturnUserById() {
        // given
        testUser = new User();
        testUser.setId(USER_ID);
        testUser.setUsername(USER_USERNAME);

        when(userRepository.findById(eq(USER_ID))).thenReturn(Optional.of(testUser));

        // when
        User user = userService.getUserById(USER_ID);

        // then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(USER_ID);
    }


    @Test
    void shouldSaveUser() {
        // given
        testUser = User.builder().username(USER_USERNAME).build();
        User user = User.builder().id(1L).username(USER_USERNAME).build();

        when(userRepository.save(eq(testUser))).thenReturn(user);
        // when
        User savedUser = userService.saveUser(testUser);

        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    void shouldRegisterUser() {
        // given
        RegistrationRequest registrationRequest = new RegistrationRequest(USER_USERNAME, USER_PASSWORD, USER_EMAIL,
                true, DeviceType.WEB);
        testUser = User.builder().username(USER_USERNAME).email(USER_EMAIL).password(USER_PASSWORD)
                .userRole(UserRole.USER).acceptTC(true).build();

        when(passwordEncoder.encode(eq(USER_PASSWORD))).thenReturn("encodedTestPassword");
        when(userRepository.save(any())).thenReturn(testUser);
        when(jwtService.generateToken(eq(Collections.EMPTY_MAP), any(UserDetailsImpl.class), eq(DeviceType.WEB))).thenReturn("jwt");

        // when
        RegistrationResult result = userService.registerUser(registrationRequest);

        // then
        assertThat(result.jwt()).isNotBlank();
        assertAll("Registration result user data validation",
                () -> assertThat(result.userRole()).isEqualTo(UserRole.USER),
                () -> assertThat(result.username()).isEqualTo(USER_USERNAME),
                () -> assertThat(result.email()).isEqualTo(USER_EMAIL)
        );
    }

    @Test
    void shouldAuthenticateUser_whenCredsAreValid() {
        // given
        LoginRequest loginRequest = new LoginRequest(USER_USERNAME, USER_PASSWORD, DeviceType.WEB);
        testUser = User.builder().id(1L).build();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.username(), loginRequest.password());

        when(userRepository.findByUsername(eq(USER_USERNAME))).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(eq(Collections.EMPTY_MAP), any(UserDetailsImpl.class), eq(DeviceType.WEB))).thenReturn("jwt");

        // when
        LoginResult result = userService.authenticateUser(loginRequest);

        // then
        assertThat(result.jwt()).isNotBlank();
        assertThat(result.username()).isEqualTo(USER_USERNAME);
    }

    @Test
    void shouldNotAuthenticateUser_andThorowUserNotFoundException_whenCredsAreNotValid() {
        // given
        LoginRequest loginRequest = new LoginRequest(USER_USERNAME, USER_PASSWORD, DeviceType.WEB);
        testUser = User.builder().id(1L).build();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.username(), loginRequest.password());

        when(userRepository.findByUsername(eq(USER_USERNAME))).thenReturn(Optional.empty());
        //when(jwtService.generateToken(eq(Collections.EMPTY_MAP), any(UserDetailsImpl.class), eq(DeviceType.WEB))).thenReturn("jwt");

        // when
        Throwable thrown = catchThrowable(() -> {
            userService.authenticateUser(loginRequest);
        });

        // then
        assertThat(thrown).isInstanceOf(UserNotFoundException.class);
    }
}
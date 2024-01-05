package com.khomishchak.ws.services;

import com.khomishchak.ws.exceptions.UserNotFoundException;
import com.khomishchak.ws.model.User;
import com.khomishchak.ws.model.enums.UserRole;
import com.khomishchak.ws.model.requests.AuthenticationRequest;
import com.khomishchak.ws.model.requests.CreateUserReq;
import com.khomishchak.ws.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
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

    private UserServiceImpl userService;

    User testUser;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, authenticationManager);
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
    void shouldCreateUser() {
        // given
        CreateUserReq registrationRequest = new CreateUserReq(USER_USERNAME, USER_PASSWORD, USER_EMAIL, true);
        testUser = User.builder().id(USER_ID).username(USER_USERNAME).email(USER_EMAIL).password(USER_PASSWORD)
                .userRole(UserRole.USER).acceptTC(true).build();

        when(passwordEncoder.encode(eq(USER_PASSWORD))).thenReturn("encodedTestPassword");
        when(userRepository.save(any())).thenReturn(testUser);

        // when
        Long result = userService.createUser(registrationRequest);

        // then
        assertThat(result).isEqualTo(USER_ID);
    }

    @Test
    void shouldAuthenticateUser_whenCredsAreValid() {
        // given
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(USER_USERNAME, USER_PASSWORD);
        testUser = User.builder().id(USER_ID).build();

        when(userRepository.findByUsername(eq(USER_USERNAME))).thenReturn(Optional.of(testUser));
        when(userRepository.save(any())).thenReturn(testUser);
        // when
        Long result = userService.authenticateUser(authenticationRequest);

        // then
        assertThat(result).isEqualTo(USER_ID);
    }

    @Test
    void shouldNotAuthenticateUser_andThorowUserNotFoundException_whenCredsAreNotValid() {
        // given
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(USER_USERNAME, USER_PASSWORD);
        testUser = User.builder().id(1L).build();

        when(userRepository.findByUsername(eq(USER_USERNAME))).thenReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> {
            userService.authenticateUser(authenticationRequest);
        });

        // then
        assertThat(thrown).isInstanceOf(UserNotFoundException.class);
    }
}
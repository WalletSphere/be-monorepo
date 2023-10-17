package com.khomishchak.cryptoportfolio.services;

import com.khomishchak.cryptoportfolio.exceptions.UserNotFoundException;
import com.khomishchak.cryptoportfolio.model.User;
import com.khomishchak.cryptoportfolio.model.enums.UserRole;
import com.khomishchak.cryptoportfolio.model.requests.LoginRequest;
import com.khomishchak.cryptoportfolio.model.requests.RegistrationRequest;
import com.khomishchak.cryptoportfolio.model.response.LoginResult;
import com.khomishchak.cryptoportfolio.model.response.RegistrationResult;
import com.khomishchak.cryptoportfolio.repositories.UserRepository;
import com.khomishchak.cryptoportfolio.security.UserDetailsImpl;
import com.khomishchak.cryptoportfolio.services.security.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private  final JwtService jwtService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public User getUserById(Long userId) {
        return getUserOrThrowException(userId);
    }


    @Override
    public RegistrationResult registerUser(RegistrationRequest registrationRequest) {

        LocalDateTime currentMoment = LocalDateTime.now();

        User newUser = User.builder()
                .username(registrationRequest.username())
                .password(passwordEncoder.encode(registrationRequest.password()))
                .email(registrationRequest.email())
                .acceptTC(registrationRequest.acceptTC())
                .createdTime(currentMoment)
                .lastLoginTime(currentMoment)
                .userRole(UserRole.NORMAL)
                .build();

        return getRegistrationResult(userRepository.save(newUser));
    }

    @Override
    public LoginResult authenticateUser(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );

        User user = userRepository.findByUsername(loginRequest.username()).orElseThrow(
                () -> new UserNotFoundException(String.format("User with username %s was not found", loginRequest.username()))
        );

        user.setLastLoginTime(LocalDateTime.now());

        return new LoginResult(loginRequest.username(), generateJwtToken(userRepository.save(user)));
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    private User getUserOrThrowException(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id: %s was not found", userId)));
    }

    private RegistrationResult getRegistrationResult(User createdUser) {

        return new RegistrationResult(createdUser.getUsername(), createdUser.getEmail(),
                createdUser.getUserRole(), generateJwtToken(createdUser));
    }

    private String generateJwtToken(User user) {
        return jwtService.generateToken(Collections.EMPTY_MAP, new UserDetailsImpl(user));
    }
}

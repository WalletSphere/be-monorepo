package com.khomishchak.cryproportfolio.services;

import com.khomishchak.cryproportfolio.exceptions.UserNotFoundException;
import com.khomishchak.cryproportfolio.model.User;
import com.khomishchak.cryproportfolio.model.enums.UserRole;
import com.khomishchak.cryproportfolio.model.requests.LoginRequest;
import com.khomishchak.cryproportfolio.model.requests.RegistrationRequest;
import com.khomishchak.cryproportfolio.model.response.LoginResult;
import com.khomishchak.cryproportfolio.model.response.RegistrationResult;
import com.khomishchak.cryproportfolio.repositories.UserRepository;
import com.khomishchak.cryproportfolio.security.UserDetailsImpl;
import com.khomishchak.cryproportfolio.services.security.JwtService;

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
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }


    @Override
    public RegistrationResult registerUser(RegistrationRequest registrationRequest) {

        LocalDateTime currentMoment = LocalDateTime.now();

        User newUser = User.builder()
                .username(registrationRequest.username())
                .password(passwordEncoder.encode(registrationRequest.password()))
                .email(registrationRequest.email())
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

    private RegistrationResult getRegistrationResult(User createdUser) {

        return new RegistrationResult(createdUser.getUsername(), createdUser.getEmail(),
                createdUser.getUserRole(), generateJwtToken(createdUser));
    }

    private String generateJwtToken(User user) {
        return jwtService.generateToken(Collections.EMPTY_MAP, new UserDetailsImpl(user));
    }
}

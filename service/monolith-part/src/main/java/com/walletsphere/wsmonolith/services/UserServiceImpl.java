package com.walletsphere.wsmonolith.services;

import com.walletsphere.wsmonolith.exceptions.UserNotFoundException;
import com.walletsphere.wsmonolith.model.User;
import com.walletsphere.wsmonolith.model.enums.UserRole;
import com.walletsphere.wsmonolith.model.requests.CreateUserReq;
import com.walletsphere.wsmonolith.model.requests.AuthenticationRequest;
import com.walletsphere.wsmonolith.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public User getUserById(Long userId) {
        return getUserOrThrowException(userId);
    }

    @Override
    public Long authenticateUser(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.username(),
                        authenticationRequest.password()
                )
        );

        User user = getUserOrThrowException(authenticationRequest.username());

        user.setLastLoginTime(LocalDateTime.now());
        return userRepository.save(user).getId();
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }


    // TODO should be validating registration data such as 'acceptTC'
    @Override
    public Long createUser(CreateUserReq request) {
        LocalDateTime currentMoment = LocalDateTime.now();
        User newUser = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .acceptTC(request.acceptTC())
                .createdTime(currentMoment)
                .lastLoginTime(currentMoment)
                .userRole(UserRole.USER)
                .build();

        return userRepository.save(newUser).getId();
    }

    private User getUserOrThrowException(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with username: %s was not found", username)));
    }

    private User getUserOrThrowException(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id: %s was not found", userId)));
    }
}

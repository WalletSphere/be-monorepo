package com.khomishchak.cryproportfolio.services;

import com.khomishchak.cryproportfolio.model.User;
import com.khomishchak.cryproportfolio.model.requests.LoginRequest;
import com.khomishchak.cryproportfolio.model.requests.RegistrationRequest;
import com.khomishchak.cryproportfolio.model.response.LoginResult;
import com.khomishchak.cryproportfolio.model.response.RegistrationResult;

import java.util.Optional;

public interface UserService {

    Optional<User> getUserById(Long userId);

    RegistrationResult registerUser(RegistrationRequest registrationRequest);

    LoginResult authenticateUser(LoginRequest loginRequest);
}

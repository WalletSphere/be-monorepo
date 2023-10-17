package com.khomishchak.cryptoportfolio.services;

import com.khomishchak.cryptoportfolio.model.User;
import com.khomishchak.cryptoportfolio.model.requests.LoginRequest;
import com.khomishchak.cryptoportfolio.model.requests.RegistrationRequest;
import com.khomishchak.cryptoportfolio.model.response.LoginResult;
import com.khomishchak.cryptoportfolio.model.response.RegistrationResult;

import java.util.Optional;

public interface UserService {

    User getUserById(Long userId);

    RegistrationResult registerUser(RegistrationRequest registrationRequest);

    LoginResult authenticateUser(LoginRequest loginRequest);

    User saveUser(User user);
}

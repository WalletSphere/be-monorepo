package com.khomishchak.ws.services;

import com.khomishchak.ws.model.User;
import com.khomishchak.ws.model.requests.LoginRequest;
import com.khomishchak.ws.model.requests.RegistrationRequest;
import com.khomishchak.ws.model.response.LoginResult;
import com.khomishchak.ws.model.response.RegistrationResult;

public interface UserService {

    User getUserById(Long userId);

    RegistrationResult registerUser(RegistrationRequest registrationRequest);

    LoginResult authenticateUser(LoginRequest loginRequest);

    User saveUser(User user);
}

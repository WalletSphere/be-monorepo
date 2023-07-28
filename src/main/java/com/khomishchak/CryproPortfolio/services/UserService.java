package com.khomishchak.CryproPortfolio.services;

import com.khomishchak.CryproPortfolio.model.requests.LoginRequest;
import com.khomishchak.CryproPortfolio.model.requests.RegistrationRequest;
import com.khomishchak.CryproPortfolio.model.response.LoginResult;
import com.khomishchak.CryproPortfolio.model.response.RegistrationResult;

public interface UserService {

    RegistrationResult registerUser(RegistrationRequest registrationRequest);

    LoginResult authenticateUser(LoginRequest loginRequest);
}

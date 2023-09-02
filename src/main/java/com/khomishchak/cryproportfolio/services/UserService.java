package com.khomishchak.cryproportfolio.services;

import com.khomishchak.cryproportfolio.model.requests.LoginRequest;
import com.khomishchak.cryproportfolio.model.requests.RegistrationRequest;
import com.khomishchak.cryproportfolio.model.response.LoginResult;
import com.khomishchak.cryproportfolio.model.response.RegistrationResult;

public interface UserService {

    RegistrationResult registerUser(RegistrationRequest registrationRequest);

    LoginResult authenticateUser(LoginRequest loginRequest);
}

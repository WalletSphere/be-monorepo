package com.khomishchak.ws.services;

import com.khomishchak.ws.model.User;
import com.khomishchak.ws.model.requests.CreateUserReq;
import com.khomishchak.ws.model.requests.AuthenticationRequest;

public interface UserService {
    User getUserById(Long userId);
    Long authenticateUser(AuthenticationRequest authenticationRequest);
    User saveUser(User user);
    Long createUser(CreateUserReq request);
}

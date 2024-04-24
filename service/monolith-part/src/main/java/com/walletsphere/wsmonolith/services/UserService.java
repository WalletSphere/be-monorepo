package com.walletsphere.wsmonolith.services;

import com.walletsphere.wsmonolith.model.User;
import com.walletsphere.wsmonolith.model.requests.CreateUserReq;
import com.walletsphere.wsmonolith.model.requests.AuthenticationRequest;

public interface UserService {
    User getUserById(Long userId);
    Long authenticateUser(AuthenticationRequest authenticationRequest);
    User saveUser(User user);
    Long createUser(CreateUserReq request);
}

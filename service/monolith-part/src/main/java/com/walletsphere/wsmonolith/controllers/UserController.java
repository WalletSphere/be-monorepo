package com.walletsphere.wsmonolith.controllers;

import com.walletsphere.wsmonolith.model.requests.CreateUserReq;
import com.walletsphere.wsmonolith.model.requests.AuthenticationRequest;
import com.walletsphere.wsmonolith.services.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public Long createUser(@RequestBody CreateUserReq request) {
        return userService.createUser(request);
    }

    @PostMapping("/authenticate")
    public Long authenticateUser(@RequestBody AuthenticationRequest request) {
        return userService.authenticateUser(request);
    }
}

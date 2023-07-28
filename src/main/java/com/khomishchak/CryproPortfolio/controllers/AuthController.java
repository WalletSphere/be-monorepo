package com.khomishchak.CryproPortfolio.controllers;

import com.khomishchak.CryproPortfolio.model.requests.LoginRequest;
import com.khomishchak.CryproPortfolio.model.requests.RegistrationRequest;
import com.khomishchak.CryproPortfolio.model.response.LoginResult;
import com.khomishchak.CryproPortfolio.model.response.RegistrationResult;
import com.khomishchak.CryproPortfolio.services.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResult> register(@RequestBody @Validated RegistrationRequest registrationRequest) {
        return new ResponseEntity<>(userService.registerUser(registrationRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResult> login(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(userService.authenticateUser(loginRequest), HttpStatus.OK);
    }
}

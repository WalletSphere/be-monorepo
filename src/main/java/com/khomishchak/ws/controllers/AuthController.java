package com.khomishchak.ws.controllers;

import com.khomishchak.ws.model.requests.LoginRequest;
import com.khomishchak.ws.model.requests.RegistrationRequest;
import com.khomishchak.ws.model.response.LoginResult;
import com.khomishchak.ws.model.response.RegistrationResult;
import com.khomishchak.ws.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResult> register(@RequestBody @Valid RegistrationRequest registrationRequest) {
        return new ResponseEntity<>(userService.registerUser(registrationRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResult> login(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(userService.authenticateUser(loginRequest), HttpStatus.OK);
    }
}

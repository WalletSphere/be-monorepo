package com.khomishchak.authservice.controller;

import com.khomishchak.authservice.model.auth.request.LoginReq;
import com.khomishchak.authservice.model.auth.request.RegistrationReq;
import com.khomishchak.authservice.model.auth.resp.LoginResp;
import com.khomishchak.authservice.model.auth.resp.ProcessedTokenResp;
import com.khomishchak.authservice.model.auth.resp.RegistrationResp;
import com.khomishchak.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResp> register(@RequestBody @Valid RegistrationReq registrationRequest) {
        return new ResponseEntity<>(authService.register(registrationRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResp> login(@RequestBody LoginReq loginRequest) {
        return new ResponseEntity<>(authService.login(loginRequest), HttpStatus.OK);
    }

    @PostMapping("/token")
    public ResponseEntity<ProcessedTokenResp> validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String tokenHeader) {
        return new ResponseEntity<>(authService.validateToken(tokenHeader), HttpStatus.OK);
    }
}
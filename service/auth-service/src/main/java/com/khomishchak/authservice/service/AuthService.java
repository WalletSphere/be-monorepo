package com.khomishchak.authservice.service;

import com.khomishchak.authservice.model.auth.request.LoginReq;
import com.khomishchak.authservice.model.auth.request.RegistrationReq;
import com.khomishchak.authservice.model.auth.resp.LoginResp;
import com.khomishchak.authservice.model.auth.resp.ProcessedTokenResp;
import com.khomishchak.authservice.model.auth.resp.RegistrationResp;

public interface AuthService {

    RegistrationResp register(RegistrationReq request);
    LoginResp login(LoginReq request);
    ProcessedTokenResp validateToken(String tokenHeader);
}

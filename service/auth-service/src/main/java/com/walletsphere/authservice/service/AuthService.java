package com.walletsphere.authservice.service;

import com.walletsphere.authservice.model.auth.request.LoginReq;
import com.walletsphere.authservice.model.auth.request.RegistrationReq;
import com.walletsphere.authservice.model.auth.resp.LoginResp;
import com.walletsphere.authservice.model.auth.resp.RegistrationResp;
import com.walletsphere.model.authentication.ProcessedJwtTokenResp;

public interface AuthService {

    RegistrationResp register(RegistrationReq request);
    LoginResp login(LoginReq request);
    ProcessedJwtTokenResp validateToken(String tokenHeader);
}

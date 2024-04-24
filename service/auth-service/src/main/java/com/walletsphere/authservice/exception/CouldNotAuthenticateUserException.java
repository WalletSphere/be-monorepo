package com.walletsphere.authservice.exception;

import com.walletsphere.authservice.model.auth.resp.ErrorResp;

public class CouldNotAuthenticateUserException extends AuthException {

    public CouldNotAuthenticateUserException(String message, ErrorResp errorResp) {
        super(message, errorResp);
    }
}

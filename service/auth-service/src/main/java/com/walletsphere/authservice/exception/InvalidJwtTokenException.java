package com.walletsphere.authservice.exception;

import com.walletsphere.authservice.model.auth.resp.ErrorResp;

public class InvalidJwtTokenException extends AuthException {
    public InvalidJwtTokenException(String message, ErrorResp errorResp) {
        super(message, errorResp);
    }
}

package com.khomishchak.authservice.exception;

import com.khomishchak.authservice.model.auth.resp.ErrorResp;

public class InvalidJwtTokenException extends AuthException {
    public InvalidJwtTokenException(String message, ErrorResp errorResp) {
        super(message, errorResp);
    }
}

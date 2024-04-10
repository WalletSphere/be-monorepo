package com.khomishchak.authservice.exception;

import com.khomishchak.authservice.model.auth.resp.ErrorResp;

public class AuthException extends RuntimeException {

    private ErrorResp errorResp;

    public AuthException(String message, ErrorResp errorResp) {
        super(message);
        this.errorResp = errorResp;
    }

    public ErrorResp getErrorResponse() {
        return errorResp;
    }
}

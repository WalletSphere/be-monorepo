package com.khomishchak.authservice.exception;

import com.khomishchak.authservice.model.auth.resp.ErrorResp;

public class CouldNotAuthenticateUserException extends AuthException {

    public CouldNotAuthenticateUserException(String message, ErrorResp errorResp) {
        super(message, errorResp);
    }
}

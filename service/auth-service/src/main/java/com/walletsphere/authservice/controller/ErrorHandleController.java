package com.walletsphere.authservice.controller;

import com.walletsphere.authservice.exception.CouldNotAuthenticateUserException;
import com.walletsphere.authservice.exception.InvalidJwtTokenException;
import com.walletsphere.authservice.model.auth.resp.ControllerHandlerErrorResp;
import com.walletsphere.authservice.model.auth.resp.ErrorResp;
import com.walletsphere.model.authentication.ProcessedJwtTokenResp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ErrorHandleController {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ControllerHandlerErrorResp> handleValidationException(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(ControllerHandlerErrorResp.fromValidationException(ex));
    }

    // TODO: replace with pattern matching for AuthException after moving to java 21
    @ExceptionHandler(CouldNotAuthenticateUserException.class)
    public ResponseEntity<ErrorResp> handleAuthenticationException(CouldNotAuthenticateUserException ex) {
            return ResponseEntity.badRequest().body(ex.getErrorResponse());
    }

    @ExceptionHandler(InvalidJwtTokenException.class)
    public ResponseEntity<?> handleAuthenticationException(InvalidJwtTokenException ex) {
        return ResponseEntity.ok().body(new ProcessedJwtTokenResp(0L, false));
    }
}

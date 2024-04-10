package com.khomishchak.ws.controllers;

import com.khomishchak.ws.exceptions.BalanceNotFoundException;
import com.khomishchak.ws.model.response.ErrorResp;
import com.khomishchak.ws.services.integration.whitebit.exceptions.WhiteBitClientException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AbstractRestController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResp> handleValidationException(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(ErrorResp.fromValidationException(ex));
    }

    @ExceptionHandler(BalanceNotFoundException.class)
    public ResponseEntity<?> handleBalanceNotFoundException(BalanceNotFoundException ex) {
        Map<String, String> result = new HashMap<>();
        result.put(ex.getClass().getName(), ex.getMessage());
        return ResponseEntity.badRequest().body(result);
    }

    @ExceptionHandler(WhiteBitClientException.class)
    public ResponseEntity<ErrorResp> handleBalanceNotFoundException(WhiteBitClientException ex) {
        return ResponseEntity.badRequest().body(ErrorResp.fromWhiteBitClientException(ex));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleBalanceNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.badRequest().body(ErrorResp.fromAuthenticationException(ex, "USERNAME_NOT_FOUND"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBalanceNotFoundException(BadCredentialsException ex) {
        return ResponseEntity.badRequest().body(ErrorResp.fromAuthenticationException(ex, "BAD_CREDENTIALS"));
    }
}

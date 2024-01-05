package com.khomishchak.authservice.controller;

import com.khomishchak.authservice.exception.AuthException;
import com.khomishchak.authservice.model.auth.resp.ControllerHandlerErrorResp;
import com.khomishchak.authservice.model.auth.resp.ErrorResp;
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
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResp> handleBalanceNotFoundException(AuthException ex) {
        return ResponseEntity.badRequest().body(ex.getErrorResponse());
    }
}

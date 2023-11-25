package com.khomishchak.ws.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class AbstractRestController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, List<String>> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        result.put("errors", errors);
        return ResponseEntity.badRequest().body(result);
    }
}

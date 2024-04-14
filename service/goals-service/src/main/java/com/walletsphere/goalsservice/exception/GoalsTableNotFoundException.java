package com.walletsphere.goalsservice.exception;

public class GoalsTableNotFoundException extends RuntimeException {

    public GoalsTableNotFoundException(String message) {
        super(message);
    }
}

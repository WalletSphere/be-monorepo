package com.khomishchak.ws.exceptions;

public class GoalsTableNotFoundException extends RuntimeException {

    public GoalsTableNotFoundException(String message) {
        super(message);
    }
}

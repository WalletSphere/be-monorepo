package com.khomishchak.ws.exceptions;

public class BalanceNotFoundException extends RuntimeException {

    public BalanceNotFoundException(String message) {
        super(message);
    }
}

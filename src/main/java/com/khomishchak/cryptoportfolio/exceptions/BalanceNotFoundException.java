package com.khomishchak.cryptoportfolio.exceptions;

public class BalanceNotFoundException extends RuntimeException {

    public BalanceNotFoundException(String message) {
        super(message);
    }
}

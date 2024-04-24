package com.walletsphere.wsmonolith.exceptions;

public class BalanceNotFoundException extends RuntimeException {

    public BalanceNotFoundException(String message) {
        super(message);
    }
}

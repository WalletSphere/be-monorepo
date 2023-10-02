package com.khomishchak.cryptoportfolio.services.integration.whitebit.exceptions;

public class WhiteBitClientException extends RuntimeException{

    private int statusCode;

    public WhiteBitClientException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

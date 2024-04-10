package com.khomishchak.ws.services.integration.whitebit.exceptions;

import java.util.List;

public class WhiteBitClientException extends RuntimeException{

    private int statusCode;
    private String code;
    private List<String> errors;

    public WhiteBitClientException(String code, String message, List<String> errors, int statusCode) {
        super(message);
        this.code = code;
        this.statusCode = statusCode;
        this.errors = errors;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getCode() {
        return code;
    }

    public List<String> getErrors() {
        return errors;
    }
}

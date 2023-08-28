package com.khomishchak.CryproPortfolio.services.markets.integration.coinMarketCap.exceptions;

public class CoinMarketCapClientException extends RuntimeException {

    private int statusCode;

    public CoinMarketCapClientException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

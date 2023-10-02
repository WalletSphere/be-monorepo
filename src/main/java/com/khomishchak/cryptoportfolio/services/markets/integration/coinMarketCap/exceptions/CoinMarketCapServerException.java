package com.khomishchak.cryptoportfolio.services.markets.integration.coinMarketCap.exceptions;

public class CoinMarketCapServerException extends RuntimeException {

    public CoinMarketCapServerException(String message) {
        super(message);
    }
}

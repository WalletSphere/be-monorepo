package com.khomishchak.cryptoportfolio.services.markets.integration.coinMarketCap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoResponse {
    private List<CryptoCurrency> data;

    @JsonCreator
    public CryptoResponse(@JsonProperty("data") List<CryptoCurrency> data) {
        this.data = data;
    }

    public List<CryptoCurrency> getData() {
        return data;
    }

    public static class CryptoCurrency {

        private String symbol;
        private final Map<String, CurrencyDetail> quote;

        @JsonCreator
        public CryptoCurrency( @JsonProperty("symbol") String symbol, @JsonProperty("quote") Map<String, CurrencyDetail> quote) {
            this.symbol = symbol;
            this.quote = quote;
        }

        public String getSymbol() {
            return symbol;
        }

        public Map<String, CurrencyDetail> getQuote() {
            return quote;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class CurrencyDetail {
            private Double currentPrice;

            @JsonCreator
            public CurrencyDetail(@JsonProperty("price") Double currentPrice) {
                this.currentPrice = currentPrice;
            }

            public CurrencyDetail() {
            }

            public Double getCurrentPrice() {
                return currentPrice;
            }
        }
    }

}

package com.khomishchak.cryptoportfolio.services.markets.integration.coinMarketCap;

import com.khomishchak.cryptoportfolio.services.markets.integration.coinMarketCap.exceptions.CoinMarketCapClientException;
import com.khomishchak.cryptoportfolio.services.markets.integration.coinMarketCap.exceptions.CoinMarketCapServerException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class CoinMarketCapServiceImpl implements CoinMarketCapService {

    private static final String BASE_URL = "https://pro-api.coinmarketcap.com/v1";
    private static final String GET_COINS_INFO_URL = "/cryptocurrency/listings/latest";

    private static final String COIN_MARKET_CAP_SERVER_ERROR_MESSAGE = "Failed to get response from CoinMarketCap, server error";
    private static final String COIN_MARKET_CAP_CLIENT_ERROR_MESSAGE = "Failed to get response from CoinMarketCap, client error";

    private final int retryMaxAttempts;
    private final Duration retryMinBackoff;

    private final WebClient webClient;

    public CoinMarketCapServiceImpl(WebClient webClient,
            @Value("${ws.integration.exchanger.api.retry.maxAttempts:2}") int retryMaxAttempts,
            @Value("${ws.integration.exchanger.api.retry.minBackoffSeconds:2}") int retryMinBackoffSeconds) {
        this.webClient = webClient;
        this.retryMaxAttempts   = retryMaxAttempts;
        this.retryMinBackoff    = Duration.ofSeconds(retryMinBackoffSeconds);
    }

    @Override
    public Mono<CryptoResponse> getCurrentMarketPrices() {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(BASE_URL + GET_COINS_INFO_URL)
                .queryParam("start", 1)
                .queryParam("limit", 100)
                .queryParam("convert", "USD");

         return webClient.get()
                 .uri(uriBuilder.build().toUri())
                 .header("X-CMC_PRO_API_KEY", "a154d1f0-db4f-4f91-805a-0fc3735e9c4d")
                 .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                 .exchangeToMono(resp -> {
                     int statusCode = resp.statusCode().value();
                     if(statusCode == 200) {
                         return resp.bodyToMono(CryptoResponse.class);
                     } else {
                         return resp.bodyToMono(String.class)
                                 .flatMap(errorMessage -> {
                                     if(statusCode >= 400 && statusCode < 500) {
                                         return Mono.error(new CoinMarketCapClientException(
                                                 String.format("%s: %s", COIN_MARKET_CAP_SERVER_ERROR_MESSAGE, errorMessage), statusCode));
                                     } else {
                                         return resp.createException().flatMap(Mono::error);
                                     }
                                 });
                     }
                 })
                 .retryWhen(Retry.backoff(retryMaxAttempts, retryMinBackoff)
                         .filter(e -> e instanceof WebClientResponseException || isTimeoutException(e))
                         .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                 new CoinMarketCapServerException(COIN_MARKET_CAP_SERVER_ERROR_MESSAGE)));
    }

    private boolean isTimeoutException(final Throwable t) {
        return t instanceof ReadTimeoutException || t instanceof ConnectTimeoutException || t instanceof WriteTimeoutException;
    }
}
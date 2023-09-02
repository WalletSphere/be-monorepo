package com.khomishchak.cryproportfolio.services.markets.integration.coinMarketCap;

import reactor.core.publisher.Mono;

public interface CoinMarketCapService {

    Mono<CryptoResponse> getCurrentMarketPrices();
}

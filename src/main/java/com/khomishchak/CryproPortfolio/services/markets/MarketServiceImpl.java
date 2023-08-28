package com.khomishchak.CryproPortfolio.services.markets;

import com.khomishchak.CryproPortfolio.services.markets.integration.coinMarketCap.CoinMarketCapService;
import com.khomishchak.CryproPortfolio.services.markets.integration.coinMarketCap.CryptoResponse;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MarketServiceImpl implements MarketService {

    private static final String USD = "USD";

    private final CoinMarketCapService coinMarketCapService;

    public MarketServiceImpl(CoinMarketCapService coinMarketCapService) {
        this.coinMarketCapService = coinMarketCapService;
    }

    @Override
    public Map<String, Double> getCurrentMarketValues() {
        return coinMarketCapService.getCurrentMarketPrices()
                .map(this::mapResponseToMap)
                // to be implemented onErrorResume()
                .block();
    }

    private Map<String, Double> mapResponseToMap(CryptoResponse cryptoResponse) {
        return cryptoResponse.getData().stream()
                .filter(c -> c.getQuote().containsKey(USD) && c.getQuote().get(USD).getCurrentPrice() != null)
                .collect(Collectors.toMap(CryptoResponse.CryptoCurrency::getSymbol, c -> c.getQuote().get(USD).getCurrentPrice()));
    }
}

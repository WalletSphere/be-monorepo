package com.khomishchak.CryproPortfolio.services.integration.whitebit;

import com.khomishchak.CryproPortfolio.services.integration.whitebit.exceptions.WhiteBitClientException;
import com.khomishchak.CryproPortfolio.services.integration.whitebit.exceptions.WhiteBitServerException;
import com.khomishchak.CryproPortfolio.model.enums.ExchangerCode;
import com.khomishchak.CryproPortfolio.model.exchanger.ApiKeySetting;
import com.khomishchak.CryproPortfolio.model.exchanger.ApiKeysPair;
import com.khomishchak.CryproPortfolio.model.exchanger.Balance;
import com.khomishchak.CryproPortfolio.model.exchanger.Currency;
import com.khomishchak.CryproPortfolio.repositories.ApiKeySettingRepository;
import com.khomishchak.CryproPortfolio.repositories.BalanceRepository;
import com.khomishchak.CryproPortfolio.repositories.UserRepository;
import com.khomishchak.CryproPortfolio.services.integration.whitebit.model.WhiteBitBalanceResp;
import com.khomishchak.CryproPortfolio.services.markets.MarketService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class WhiteBitServiceImpl implements WhiteBitService {

    public static final String GET_MAIN_BALANCE_URL = "/api/v4/main-account/balance";
    public static final String BASE_URL = "https://whitebit.com";

    private static final String WHITE_BIT_SERVER_ERROR_MESSAGE = "Failed to get response from WhiteBit, server error";
    private static final String WHITE_BIT_CLIENT_ERROR_MESSAGE = "Failed to get response from WhiteBit, client error";

    public static final ExchangerCode code = ExchangerCode.WHITE_BIT;

    private final UserRepository userRepository;
    private final ApiKeySettingRepository apiKeySettingRepository;
    private final BalanceRepository balanceRepository;
    private final WebClient webClient;
    private final int retryMaxAttempts;
    private final Duration retryMinBackoff;
    private final MarketService marketService;

    public WhiteBitServiceImpl(UserRepository userRepository, BalanceRepository balanceRepository,
            @Qualifier("WhiteBitApiWebClient") WebClient webClient, ApiKeySettingRepository apiKeySettingRepository,
            @Value("${crypto.portfolio.integration.exchanger.api.retry.maxAttempts:2}") int retryMaxAttempts,
            @Value("${crypto.portfolio.integration.exchanger.api.retry.minBackoffSeconds:2}") int retryMinBackoffSeconds,
            MarketService marketService) {
        this.userRepository     = userRepository;
        this.apiKeySettingRepository = apiKeySettingRepository;
        this.balanceRepository  = balanceRepository;
        this.webClient          = webClient;
        this.retryMaxAttempts   = retryMaxAttempts;
        this.retryMinBackoff    = Duration.ofSeconds(retryMinBackoffSeconds);
        this.marketService      = marketService;
    }

    @Override
    public Balance getAccountBalance(long accoId) {
        List<ApiKeySetting> apiKeys = apiKeySettingRepository.findAllByUserId(accoId);

        ApiKeysPair keysPair = apiKeys.stream()
                .filter(keys -> code.equals(keys.getCode()))
                .map(ApiKeySetting::getApiKeys).findFirst().get();

        String apiKey = keysPair.getPublicApi();

        validateApiKey(apiKey, code);

        String requestJson = String.format("{\"request\":\"%1$s\",\"nonce\":\"%2$s\",\"nonceWindow\":false}",
                GET_MAIN_BALANCE_URL,
                System.currentTimeMillis());

        String payload = Base64.getEncoder().encodeToString(requestJson.getBytes());
        String signature = null;

        try {
            signature = calcSignature(payload, keysPair.getPrivateKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(BASE_URL + GET_MAIN_BALANCE_URL);

        WhiteBitBalanceResp response = webClient.post()
                .uri(uriBuilder.build().toUri())
                .bodyValue(requestJson)
                .header("Content-type", "application/json")
                .header("X-TXC-APIKEY", apiKey)
                .header("X-TXC-PAYLOAD", payload)
                .header("X-TXC-SIGNATURE", signature)
                .exchangeToMono(resp -> {
                    int statusCode = resp.statusCode().value();
                    if(HttpStatus.OK.equals(resp.statusCode())) {
                        return resp.bodyToMono(WhiteBitBalanceResp.class);
                    } else {
                        return resp.bodyToMono(String.class)
                                .flatMap(errorMessage -> {
                                    if(statusCode >= 400 && statusCode < 500) {
                                        return Mono.error(new WhiteBitClientException(String.format("%s: %s", WHITE_BIT_CLIENT_ERROR_MESSAGE, errorMessage), statusCode));
                                    } else {
                                        return Mono.error(new WhiteBitServerException(errorMessage));
                                    }
                                });

                    }
                })
                .retryWhen(Retry.backoff(retryMaxAttempts, retryMinBackoff)
                        .filter(e -> e instanceof WebClientResponseException || isTimeoutException(e))
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                new WhiteBitServerException(WHITE_BIT_SERVER_ERROR_MESSAGE)))
                .block();

        List<Currency> availableCurrencies = mapToCurrencies(response.getCurrencies());

        Balance balance = Balance.builder()
                .code(code)
                .user(userRepository.getReferenceById(accoId))
                .totalValue(getTotalPrices(availableCurrencies))
                .currencies(availableCurrencies)
                .build();

        return balanceRepository.save(balance);
    }

    private Double getTotalPrices(List<Currency> currencies) {


        Map<String, Double> marketValues = marketService.getCurrentMarketValues();
        Map<String, Currency> currencyMap = currencies.stream()
                .collect(Collectors.toMap(Currency::getCurrencyCode, Function.identity()));


        double totalValue = 0;
        for (Map.Entry<String, Double> entry : marketValues.entrySet()) {
            Currency currency = currencyMap.get(entry.getKey());
            if (currency != null) {
                double currencyTotalValue = entry.getValue() * currency.getAmount();
                currency.setTotalValue(currencyTotalValue);
                totalValue += currencyTotalValue;
            }
        }

        return totalValue;
    }

    private List<Currency> mapToCurrencies (Map<String, String> resp) {
        List<Currency> result  = new ArrayList<>();

        for(Map.Entry<String, String> unit: resp.entrySet()) {
            if(unit.getValue().equals("0")) {
                continue;
            }
            Currency currency = new Currency();
            currency.setCurrencyCode(unit.getKey());
            currency.setAmount(Double.parseDouble(unit.getValue()));

            result.add(currency);
        }

        return result;
    }

    private static String calcSignature(String data, String secretKey)
            throws NoSuchAlgorithmException, InvalidKeyException {

        final String HMAC_SHA512 = "HmacSHA512";
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), HMAC_SHA512);
        Mac mac = Mac.getInstance(HMAC_SHA512);
        mac.init(secretKeySpec);

        byte[] bytes = mac.doFinal(data.getBytes());
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    private void validateApiKey(String apiKey, ExchangerCode code) {
        Assert.hasText(apiKey, String.format("IP Key with for code: %s not present", code.name()));
    }

    private boolean isTimeoutException(final Throwable t) {
        return t instanceof ReadTimeoutException || t instanceof ConnectTimeoutException || t instanceof WriteTimeoutException;
    }
}

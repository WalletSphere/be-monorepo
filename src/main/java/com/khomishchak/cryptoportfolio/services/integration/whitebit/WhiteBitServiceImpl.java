package com.khomishchak.cryptoportfolio.services.integration.whitebit;

import com.khomishchak.cryptoportfolio.model.DepositWithdrawalTransaction;

import com.khomishchak.cryptoportfolio.services.integration.whitebit.exceptions.WhiteBitClientException;
import com.khomishchak.cryptoportfolio.services.integration.whitebit.exceptions.WhiteBitServerException;
import com.khomishchak.cryptoportfolio.model.enums.ExchangerCode;
import com.khomishchak.cryptoportfolio.model.exchanger.ApiKeySetting;
import com.khomishchak.cryptoportfolio.model.exchanger.ApiKeysPair;
import com.khomishchak.cryptoportfolio.model.exchanger.Balance;
import com.khomishchak.cryptoportfolio.model.exchanger.Currency;
import com.khomishchak.cryptoportfolio.repositories.ApiKeySettingRepository;
import com.khomishchak.cryptoportfolio.repositories.BalanceRepository;
import com.khomishchak.cryptoportfolio.repositories.UserRepository;
import com.khomishchak.cryptoportfolio.services.integration.whitebit.mappers.WhiteBitResponseMapper;
import com.khomishchak.cryptoportfolio.services.integration.whitebit.model.WhiteBitBalanceResp;
import com.khomishchak.cryptoportfolio.services.integration.whitebit.model.WhiteBitDepositWithdrawalHistoryResp;
import com.khomishchak.cryptoportfolio.services.markets.MarketService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
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
    public static final String GET_MAIN_BALANCE_DEPOSIT_WITHDRAWAL_HISTORY_URL = "/api/v4/main-account/history";
    public static final String BASE_URL = "https://whitebit.com";

    private static final String WHITE_BIT_SERVER_ERROR_MESSAGE = "Failed to get response from WhiteBit, server error";
    private static final String WHITE_BIT_CLIENT_ERROR_MESSAGE = "Failed to get response from WhiteBit, client error";

    private static final ExchangerCode CODE = ExchangerCode.WHITE_BIT;

    private final UserRepository userRepository;
    private final ApiKeySettingRepository apiKeySettingRepository;
    private final BalanceRepository balanceRepository;
    private final WebClient webClient;
    private final int retryMaxAttempts;
    private final Duration retryMinBackoff;
    private final MarketService marketService;
    private final WhiteBitResponseMapper responseMapper;

    public WhiteBitServiceImpl(UserRepository userRepository, BalanceRepository balanceRepository,
            @Qualifier("WhiteBitApiWebClient") WebClient webClient, ApiKeySettingRepository apiKeySettingRepository,
            @Value("${crypto.portfolio.integration.exchanger.api.retry.maxAttempts:2}") int retryMaxAttempts,
            @Value("${crypto.portfolio.integration.exchanger.api.retry.minBackoffSeconds:2}") int retryMinBackoffSeconds,
            MarketService marketService, WhiteBitResponseMapper responseMapper) {
        this.userRepository     = userRepository;
        this.apiKeySettingRepository = apiKeySettingRepository;
        this.balanceRepository  = balanceRepository;
        this.webClient          = webClient;
        this.retryMaxAttempts   = retryMaxAttempts;
        this.retryMinBackoff    = Duration.ofSeconds(retryMinBackoffSeconds);
        this.marketService      = marketService;
        this.responseMapper     = responseMapper;
    }

    @Override
    public Balance getAccountBalance(long userId) {
        ApiKeysPair keysPair = getApiKeysPair(userId);
        String apiKey = keysPair.getPublicApi();
        validateApiKey(apiKey);

        String requestJson = String.format("{\"request\":\"%1$s\",\"nonce\":\"%2$s\",\"nonceWindow\":false}",
                GET_MAIN_BALANCE_URL,
                System.currentTimeMillis());

        WhiteBitBalanceResp response =
                makeWebPostRequest(GET_MAIN_BALANCE_URL, requestJson, keysPair, WhiteBitBalanceResp.class);
        List<Currency> availableCurrencies = responseMapper.mapToCurrencies(response);

        Balance balance = Balance.builder()
                .code(CODE)
                .user(userRepository.getReferenceById(userId))
                .totalValue(getTotalPrices(availableCurrencies))
                .currencies(availableCurrencies)
                .build();

        return balanceRepository.save(balance);
    }

    @Override
    public List<DepositWithdrawalTransaction> getDepositWithdrawalHistory(long userId) {
        ApiKeysPair keysPair = getApiKeysPair(userId);
        String apiKey = keysPair.getPublicApi();
        validateApiKey(apiKey);

        String requestJson = String.format(
                "{\"offset\":%1$d,\"limit\":%2$d,\"request\":\"%3$s\",\"nonce\":\"%4$s\"}",
                0, 100, GET_MAIN_BALANCE_DEPOSIT_WITHDRAWAL_HISTORY_URL, System.currentTimeMillis()
        );

        WhiteBitDepositWithdrawalHistoryResp response =
                makeWebPostRequest(GET_MAIN_BALANCE_DEPOSIT_WITHDRAWAL_HISTORY_URL, requestJson, keysPair,
                        WhiteBitDepositWithdrawalHistoryResp.class);

        return responseMapper.mapWithdrawalDepositHistoryToTransactions(response);
    }

    private <T> T makeWebPostRequest(String uri, String requestJson, ApiKeysPair keysPair, Class<T> responseType) {
        String payload = Base64.getEncoder().encodeToString(requestJson.getBytes());
        String signature;
        try {
            signature = calcSignature(payload, keysPair.getPrivateKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(BASE_URL + uri);

        return webClient.post()
                .uri(uriBuilder.build().toUri())
                .bodyValue(requestJson)
                .header("Content-type", "application/json")
                .header("X-TXC-APIKEY", keysPair.getPublicApi())
                .header("X-TXC-PAYLOAD", payload)
                .header("X-TXC-SIGNATURE", signature)
                .exchangeToMono(resp -> {
                    int statusCode = resp.statusCode().value();
                    if (HttpStatus.OK.equals(resp.statusCode())) {
                        return resp.bodyToMono(responseType);
                    } else {
                        return handleErrorResponse(resp, statusCode);
                    }
                })
                .retryWhen(Retry.backoff(retryMaxAttempts, retryMinBackoff)
                        .filter(e -> e instanceof WebClientResponseException || isTimeoutException(e))
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                new WhiteBitServerException(WHITE_BIT_SERVER_ERROR_MESSAGE)))
                .block();
    }

    private <T> Mono<T> handleErrorResponse(ClientResponse resp, int statusCode) {
        return resp.bodyToMono(String.class)
                .flatMap(errorMessage -> {
                    if (statusCode >= 400 && statusCode < 500) {
                        return Mono.error(new WhiteBitClientException(String.format("%s: %s", WHITE_BIT_CLIENT_ERROR_MESSAGE, errorMessage), statusCode));
                    } else {
                        return Mono.error(new WhiteBitServerException(errorMessage));
                    }
                });
    }

    private ApiKeysPair getApiKeysPair(long userId) {
        List<ApiKeySetting> apiKeys = apiKeySettingRepository.findAllByUserId(userId);

        return apiKeys.stream()
                .filter(keys -> CODE.equals(keys.getCode()))
                .map(ApiKeySetting::getApiKeys).findFirst().get();
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

    private void validateApiKey(String apiKey) {
        Assert.hasText(apiKey, String.format("IP Key with for code: %s not present", CODE));
    }

    private boolean isTimeoutException(final Throwable t) {
        return t instanceof ReadTimeoutException || t instanceof ConnectTimeoutException || t instanceof WriteTimeoutException;
    }
}

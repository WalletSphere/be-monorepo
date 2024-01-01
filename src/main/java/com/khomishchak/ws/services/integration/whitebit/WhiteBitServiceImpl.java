package com.khomishchak.ws.services.integration.whitebit;

import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.model.exchanger.DecryptedApiKeySettingDTO;
import com.khomishchak.ws.repositories.ApiKeySettingRepository;
import com.khomishchak.ws.services.exchangers.apikeys.ApiKeySettingService;
import com.khomishchak.ws.services.integration.whitebit.exceptions.WhiteBitClientException;
import com.khomishchak.ws.services.integration.whitebit.exceptions.WhiteBitServerException;
import com.khomishchak.ws.services.integration.whitebit.mappers.WhiteBitErrorResponseMapper;
import com.khomishchak.ws.services.integration.whitebit.model.WhiteBitBalanceResp;
import com.khomishchak.ws.services.integration.whitebit.model.WhiteBitDepositWithdrawalHistoryResp;
import com.khomishchak.ws.services.integration.whitebit.model.WhiteBitV4ErrorResp;
import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Formatter;
import java.util.List;

@Service
public class WhiteBitServiceImpl implements WhiteBitService {

    private String getMainBalanceUrl;
    private String baseUrl;

    private String getMainBalanceDepositWithdrawalHistoryUrl;

    private static final String WHITE_BIT_SERVER_ERROR_MESSAGE = "Failed to get response from WhiteBit, server error";
    private static final ExchangerCode CODE = ExchangerCode.WHITE_BIT;
    private final WhiteBitErrorResponseMapper errorResponseMapper;
    private final ApiKeySettingService apiKeySettingService;
    private final WebClient webClient;
    private final int retryMaxAttempts;
    private final Duration retryMinBackoff;

    public WhiteBitServiceImpl(@Qualifier("WhiteBitApiWebClient") WebClient webClient,
                               WhiteBitErrorResponseMapper errorResponseMapper,
                               ApiKeySettingService apiKeySettingService,
            @Value("${ws.integration.exchanger.api.retry.maxAttempts:2}") int retryMaxAttempts,
            @Value("${ws.integration.exchanger.api.retry.minBackoffSeconds:2}") int retryMinBackoffSeconds) {
        this.apiKeySettingService = apiKeySettingService;
        this.webClient = webClient;
        this.retryMaxAttempts = retryMaxAttempts;
        this.retryMinBackoff = Duration.ofSeconds(retryMinBackoffSeconds);
        this.errorResponseMapper = errorResponseMapper;
    }

    @Value("${ws.integration.exchanger.white-bit.base-url}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Value("${ws.integration.exchanger.white-bit.get-deposit-withdraw-history}")
    public void setGetMainBalanceDepositWithdrawalHistoryUrl(String getMainBalanceDepositWithdrawalHistoryUrl) {
        this.getMainBalanceDepositWithdrawalHistoryUrl = getMainBalanceDepositWithdrawalHistoryUrl;
    }

    @Value("${ws.integration.exchanger.white-bit.get-main-balance-url}")
    public void setGetMainBalanceUrl(String getMainBalanceUrl) {
        this.getMainBalanceUrl = getMainBalanceUrl;
    }

    @Override
    public Mono<WhiteBitBalanceResp> getAccountBalance(long userId) {
        DecryptedApiKeySettingDTO decryptedKeysPair = getApiKeysPair(userId);
        String apiKey = decryptedKeysPair.getPublicKey();
        validateApiKey(apiKey);

        // TODO: create POJO
        String requestJson = String.format("{\"request\":\"%1$s\",\"nonce\":\"%2$s\",\"nonceWindow\":false}",
                getMainBalanceUrl,
                System.currentTimeMillis());

        return makeWebPostRequest(getMainBalanceUrl, requestJson, decryptedKeysPair, WhiteBitBalanceResp.class);
    }

    @Override
    public Mono<WhiteBitDepositWithdrawalHistoryResp> getDepositWithdrawalHistory(long userId) {
        DecryptedApiKeySettingDTO keysPair = getApiKeysPair(userId);
        String apiKey = keysPair.getPublicKey();
        validateApiKey(apiKey);

        // TODO: create POJO
        String requestJson = String.format(
                "{\"offset\":%1$d,\"limit\":%2$d,\"request\":\"%3$s\",\"nonce\":\"%4$s\"}",
                0, 100, getMainBalanceDepositWithdrawalHistoryUrl, System.currentTimeMillis()
        );

        return makeWebPostRequest(getMainBalanceDepositWithdrawalHistoryUrl, requestJson, keysPair,
                        WhiteBitDepositWithdrawalHistoryResp.class);
    }

    private <T> Mono<T> makeWebPostRequest(String uri, String requestJson, DecryptedApiKeySettingDTO keysPair, Class<T> responseType) {
        String payload = Base64.getEncoder().encodeToString(requestJson.getBytes());
        String signature = calculateSignature(payload, keysPair);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl + uri);

        return webClient.post()
                .uri(uriBuilder.build().toUri())
                .bodyValue(requestJson)
                .header("Content-type", "application/json")
                .header("X-TXC-APIKEY", keysPair.getPublicKey())
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
                                new WhiteBitServerException(WHITE_BIT_SERVER_ERROR_MESSAGE)));
    }

    private <T> Mono<T> handleErrorResponse(ClientResponse resp, int statusCode) {
        return resp.bodyToMono(String.class)
                .map(errorMessage -> errorResponseMapper.mapPlainTextErrorToObj(errorMessage, WhiteBitV4ErrorResp.class))
                .flatMap(errorMessageResp -> {
                    if (statusCode >= 400 && statusCode < 500) {
                        return getErrorResp(errorMessageResp, statusCode);
                    } else {
                        return resp.createException().flatMap(Mono::error);
                    }
                });
    }

    // TODO: should be able to return multiple keySettings for the same code but with different balance names
    private DecryptedApiKeySettingDTO getApiKeysPair(long userId) {
        List<DecryptedApiKeySettingDTO> apiKeys = apiKeySettingService.getDecryptApiKeySettings(userId);

        return apiKeys.stream()
                .filter(keys -> CODE.equals(keys.getCode()))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException(String.format("API Keys with for code: %s not present", CODE)));
    }

    private <T> Mono<T> getErrorResp(WhiteBitV4ErrorResp resp, int statusCode) {
        return Mono.error(new WhiteBitClientException(resp.getCode(), resp.getErrorMessage(), getErrors(resp), statusCode));
    }

    private List<String> getErrors (WhiteBitV4ErrorResp resp) {
        WhiteBitV4ErrorResp.Errors respErrors = resp.getErrors();
        return respErrors != null ? respErrors.getMessages() : new ArrayList<>();
    }

    private String calculateSignature(String payload, DecryptedApiKeySettingDTO keysPair) {
        try {
            return calcSignature(payload, keysPair.getPrivateKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        Assert.hasText(apiKey, String.format("API Key with for code: %s not present", CODE));
    }

    private boolean isTimeoutException(final Throwable t) {
        return t instanceof ReadTimeoutException || t instanceof ConnectTimeoutException || t instanceof WriteTimeoutException;
    }
}

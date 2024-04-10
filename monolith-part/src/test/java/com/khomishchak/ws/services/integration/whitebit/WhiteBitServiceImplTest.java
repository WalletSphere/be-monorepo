package com.khomishchak.ws.services.integration.whitebit;

import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.model.exchanger.DecryptedApiKeySettingDTO;
import com.khomishchak.ws.services.exchangers.apikeys.ApiKeySettingService;
import com.khomishchak.ws.services.integration.whitebit.exceptions.WhiteBitClientException;
import com.khomishchak.ws.services.integration.whitebit.exceptions.WhiteBitServerException;
import com.khomishchak.ws.services.integration.whitebit.mappers.WhiteBitErrorResponseMapper;
import com.khomishchak.ws.services.integration.whitebit.model.WhiteBitBalanceResp;
import com.khomishchak.ws.services.integration.whitebit.model.WhiteBitDepositWithdrawalHistoryResp;
import com.khomishchak.ws.services.integration.whitebit.model.WhiteBitV4ErrorResp;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class WhiteBitServiceImplTest {

    private static final long USER_ID = 1L;
    private static final int RETRY_MAX_ATTEMPTS = 1;
    private static final int RETRY_MIN_BACKOFF_SECONDS = 1;
    private static final String PRIVATE_KEY = "privateKey";
    private static final String PUBLIC_KEY = "publicKey";

    private MockWebServer mockWebServer;

    private WebClient webClient;

    @Mock
    private ApiKeySettingService apiKeySettingService;
    @Mock
    private WhiteBitErrorResponseMapper errorResponseMapper;

    private WhiteBitServiceImpl whiteBitService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        webClient = WebClient.builder().build();

        whiteBitService = new WhiteBitServiceImpl(webClient, errorResponseMapper, apiKeySettingService, RETRY_MAX_ATTEMPTS,
                RETRY_MIN_BACKOFF_SECONDS);
        whiteBitService.setBaseUrl(mockWebServer.url("/").url().toString());
        whiteBitService.setGetMainBalanceDepositWithdrawalHistoryUrl("/transactions");
        whiteBitService.setGetMainBalanceUrl("/main-balance");
    }

    @Test
    void shouldReturnMainBalance() {
        // given
        List<DecryptedApiKeySettingDTO> decryptedKeysPair = List.of(DecryptedApiKeySettingDTO.builder()
                        .publicKey(PUBLIC_KEY).privateKey(PRIVATE_KEY).code(ExchangerCode.WHITE_BIT).build());

        Map<String, String> expectedCurrencies = new HashMap<>();
        expectedCurrencies.put("BSV", "1.3"); expectedCurrencies.put("BTC", "22.11"); expectedCurrencies.put("BTG", "0");
        expectedCurrencies.put("BTT", "0"); expectedCurrencies.put("XLM", "36.48");

        when(apiKeySettingService.getDecryptApiKeySettings(USER_ID)).thenReturn(decryptedKeysPair);

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(getBalanceRespJson())
                .addHeader("Content-Type", "application/json"));

        // when
        Mono<WhiteBitBalanceResp> resultMono = whiteBitService.getAccountBalance(USER_ID);

        // then
        StepVerifier.create(resultMono)
                .assertNext(result -> {
                    Assertions.assertThat(result).isNotNull();
                    Assertions.assertThat(result.getCurrencies()).isEqualTo(expectedCurrencies);
                })
                .expectComplete()
                .verify();
    }

    @Test
    void shouldReturnDepositWithdrawalHistory() {
        // given
        List<DecryptedApiKeySettingDTO> decryptedApiKeySetting = List.of(DecryptedApiKeySettingDTO.builder()
                .publicKey(PUBLIC_KEY).privateKey(PRIVATE_KEY).code(ExchangerCode.WHITE_BIT).build());

        List<WhiteBitDepositWithdrawalHistoryResp.Record> expectedRecords = List.of(new WhiteBitDepositWithdrawalHistoryResp.Record(
                "3ApEASLcrQtZpg1TsssFgYF5V5YQJAKvuE", 1593437922L, "BTC", 1, 0.0006,
                15, "5e112b38-9652-11ed-a1eb-0242ac120002",
                "a275a514013e4e0f927fd0d1bed215e7f6f2c4c6ce762836fe135ec22529d886"));

        when(apiKeySettingService.getDecryptApiKeySettings(USER_ID)).thenReturn(decryptedApiKeySetting);

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(getDepositWithdrawalHistoryResp())
                .addHeader("Content-Type", "application/json"));

        // when
        Mono<WhiteBitDepositWithdrawalHistoryResp> resultMono = whiteBitService.getDepositWithdrawalHistory(USER_ID);

        // then
        StepVerifier.create(resultMono)
                .assertNext(result -> {
                    Assertions.assertThat(result).isNotNull();
                    Assertions.assertThat(result.getRecords()).isEqualTo(expectedRecords);
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void shouldThrowServerException_whenWhiteBitRespCodeIs500_fromLocalEndpoint() {
        // Given
        String errorMessage = "Failed to get response from WhiteBit, server error";

        WhiteBitV4ErrorResp.Errors errors = new WhiteBitV4ErrorResp.Errors();
        errors.setMessages(Map.of("param", "error"));

        WhiteBitV4ErrorResp whiteBitV4ErrorResp = new WhiteBitV4ErrorResp("0", errorMessage, errors);

        List<DecryptedApiKeySettingDTO> decryptedKeysPair = List.of(DecryptedApiKeySettingDTO.builder()
                .publicKey(PUBLIC_KEY).privateKey(PRIVATE_KEY).code(ExchangerCode.WHITE_BIT).build());

        when(apiKeySettingService.getDecryptApiKeySettings(USER_ID)).thenReturn(decryptedKeysPair);
        when(errorResponseMapper.mapPlainTextErrorToObj(eq(errorMessage), eq(WhiteBitV4ErrorResp.class)))
                .thenReturn(whiteBitV4ErrorResp);

        final MockResponse serverErrorMockResponse = new MockResponse()
                .setResponseCode(500)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(errorMessage);

        mockWebServer.enqueue(serverErrorMockResponse);
        // Preparing two responses because we expect a retry
        mockWebServer.enqueue(serverErrorMockResponse);

        // When
        Exception exception = assertThrows(WhiteBitServerException.class, () -> {
            whiteBitService.getAccountBalance(USER_ID).block();
        });

        // Then
        // Verifying we retried
        assertThat(mockWebServer.getRequestCount()).isEqualTo(2);
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    public void shouldThrowServerException_whenWhiteBitRespCodeIs400_fromLocalEndpoint() {
        // Given
        // Prepare the MockResponse
        String errorMessage = "errorMessage";

        WhiteBitV4ErrorResp.Errors errors = new WhiteBitV4ErrorResp.Errors();
        errors.setMessages(Map.of("param", "error"));

        WhiteBitV4ErrorResp whiteBitV4ErrorResp = new WhiteBitV4ErrorResp("0", errorMessage, errors);

        List<DecryptedApiKeySettingDTO> decryptedKeysPair = List.of(DecryptedApiKeySettingDTO.builder()
                .publicKey(PUBLIC_KEY).privateKey(PRIVATE_KEY).code(ExchangerCode.WHITE_BIT).build());

        when(apiKeySettingService.getDecryptApiKeySettings(USER_ID)).thenReturn(decryptedKeysPair);
        when(errorResponseMapper.mapPlainTextErrorToObj(eq(errorMessage), eq(WhiteBitV4ErrorResp.class)))
                .thenReturn(whiteBitV4ErrorResp);

        final MockResponse serverErrorMockResponse = new MockResponse()
                .setResponseCode(400)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(errorMessage);

        mockWebServer.enqueue(serverErrorMockResponse);

        // When
        WhiteBitClientException exception = assertThrows(WhiteBitClientException.class, () -> {
            whiteBitService.getAccountBalance(USER_ID).block();
        });

        // Then
        // Verifying we retried
        assertThat(mockWebServer.getRequestCount()).isEqualTo(1);
        assertThat(exception.getCode()).isEqualTo("0");
        assertThat(exception.getErrors()).isEqualTo(errors.getMessages());
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }



    private String getBalanceRespJson() {
         return """
        {
            "BSV": {
                "main_balance": "1.3"
            },
            "BTC": {
                "main_balance": "22.11"
            },
            "BTG": {
                "main_balance": "0"
            },
            "BTT": {
                "main_balance": "0"
            },
            "XLM": {
                "main_balance": "36.48"
            }
        }""";
    }

    private String getDepositWithdrawalHistoryResp() {
        return """
                {
                    "limit": 100,
                    "offset": 0,
                    "records": [
                        {
                            "address": "3ApEASLcrQtZpg1TsssFgYF5V5YQJAKvuE",                                        
                            "uniqueId": null,                                                                       
                            "createdAt": 1593437922,                                                                
                            "currency": "Bitcoin",                                                                  
                            "ticker": "BTC",                                                                        
                            "method": 1,                                                                            
                            "amount": "0.0006",                                                                     
                            "description": "",                                                                      
                            "memo": "",                                                                             
                            "fee": "0",                                                                             
                            "status": 15,                                                                           
                            "network": null,                                                                        
                            "transactionHash": "a275a514013e4e0f927fd0d1bed215e7f6f2c4c6ce762836fe135ec22529d886",  
                            "transactionId": "5e112b38-9652-11ed-a1eb-0242ac120002",                                
                            "details": {
                                "partial": {                                                                        
                                    "requestAmount": "50000",                                                       
                                    "processedAmount": "39000",                                                     
                                    "processedFee": "273",                                                          
                                    "normalizeTransaction": ""                                                      
                                }
                            },
                            "confirmations": {                                                                      
                                "actual": 1,                                                                        
                                "required": 2                                                                      
                            }
                        }
                    ],
                    "total": 300                                                                                   
                }
                """;
    }
}
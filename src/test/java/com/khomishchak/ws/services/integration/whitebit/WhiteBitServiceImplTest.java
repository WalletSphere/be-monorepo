package com.khomishchak.ws.services.integration.whitebit;

import com.khomishchak.ws.adapters.ApiKeySettingRepositoryAdapter;
import com.khomishchak.ws.model.TransferTransactionType;
import com.khomishchak.ws.model.User;
import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.model.exchanger.Balance;
import com.khomishchak.ws.model.exchanger.Currency;
import com.khomishchak.ws.model.exchanger.DecryptedApiKeySettingDTO;
import com.khomishchak.ws.model.exchanger.transaction.DepositWithdrawalTransaction;
import com.khomishchak.ws.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import com.khomishchak.ws.repositories.BalanceRepository;
import com.khomishchak.ws.repositories.DepositWithdrawalTransactionsHistoryRepository;
import com.khomishchak.ws.services.integration.whitebit.exceptions.WhiteBitClientException;
import com.khomishchak.ws.services.integration.whitebit.exceptions.WhiteBitServerException;
import com.khomishchak.ws.services.integration.whitebit.mappers.WhiteBitResponseMapper;
import com.khomishchak.ws.services.integration.whitebit.model.WhiteBitBalanceResp;
import com.khomishchak.ws.services.integration.whitebit.model.WhiteBitDepositWithdrawalHistoryResp;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class WhiteBitServiceImplTest {

    private static final long USER_ID = 1L;
    private static final long BALANCE_ID = 2L;
    private static final int RETRY_MAX_ATTEMPTS = 1;
    private static final int RETRY_MIN_BACKOFF_SECONDS = 1;
    private static final String PRIVATE_KEY = "privateKey";
    private static final String PUBLIC_KEY = "publicKey";

    private MockWebServer mockWebServer;

    private WebClient webClient;

    @Mock
    private ApiKeySettingRepositoryAdapter apiKeySettingRepositoryAdapter;
    @Mock
    private BalanceRepository balanceRepository;
    @Mock
    private WhiteBitResponseMapper responseMapper;
    @Mock
    private DepositWithdrawalTransactionsHistoryRepository depositWithdrawalTransactionsHistoryRepository;

    private WhiteBitServiceImpl whiteBitService;

    private User testUser;

    @BeforeEach
    void setUp() throws IOException {
        testUser = User.builder().id(USER_ID).build();
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        webClient = WebClient.builder().build();

        whiteBitService = new WhiteBitServiceImpl(balanceRepository, webClient, apiKeySettingRepositoryAdapter,
                RETRY_MAX_ATTEMPTS, RETRY_MIN_BACKOFF_SECONDS, responseMapper, depositWithdrawalTransactionsHistoryRepository);
        whiteBitService.setBaseUrl(mockWebServer.url("/").url().toString());
        whiteBitService.setGetMainBalanceDepositWithdrawalHistoryUrl("/transactions");
        whiteBitService.setGetMainBalanceUrl("/main-balance");
    }

    @Test
    void shouldReturnMainBalance() {
        // given
        Currency bsv = new Currency("BSV", 1.3);
        Currency btc = new Currency("BTC", 22.11);
        Currency xlm = new Currency("XLM", 36.48);
        List<Currency> currencies = List.of(bsv, btc, xlm);

        List<DecryptedApiKeySettingDTO> decryptedKeysPair = List.of(DecryptedApiKeySettingDTO.builder()
                        .publicKey(PUBLIC_KEY).privateKey(PRIVATE_KEY).code(ExchangerCode.WHITE_BIT).build());

        Balance balance = Balance.builder().id(BALANCE_ID).build();

        when(apiKeySettingRepositoryAdapter.findAllByUserId(USER_ID)).thenReturn(decryptedKeysPair);
        when(responseMapper.mapToCurrencies(any(WhiteBitBalanceResp.class))).thenReturn(currencies);
        when(balanceRepository.findByCodeAndUser_Id(ExchangerCode.WHITE_BIT, USER_ID)).thenReturn(Optional.of(balance));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(getBalanceRespJson())
                .addHeader("Content-Type", "application/json"));

        // when
        Balance result = whiteBitService.getAccountBalance(USER_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCurrencies()).isEqualTo(currencies);
    }

    @Test
    void shouldReturnDepositWithdrawalHistory() {
        // given
        List<DecryptedApiKeySettingDTO> decryptedApiKeySetting = List.of(DecryptedApiKeySettingDTO.builder()
                .publicKey(PUBLIC_KEY).privateKey(PRIVATE_KEY).code(ExchangerCode.WHITE_BIT).build());

        List<DepositWithdrawalTransaction> transactions = List.of(
                DepositWithdrawalTransaction.depositWithdrawalTransactionBuilder()
                .transactionId("5e112b38-9652-11ed-a1eb-0242ac120002")
                .transactionHash("a275a514013e4e0f927fd0d1bed215e7f6f2c4c6ce762836fe135ec22529d886")
                .amount(BigDecimal.valueOf(0.0006))
                .ticker("BTC")
                .transferTransactionType(TransferTransactionType.DEPOSIT)
                .build());

        Balance balance = Balance.builder().id(BALANCE_ID).user(testUser).build();

        when(apiKeySettingRepositoryAdapter.findAllByUserId(USER_ID)).thenReturn(decryptedApiKeySetting);
        when(responseMapper.mapWithdrawalDepositHistoryToTransactions(any(WhiteBitDepositWithdrawalHistoryResp.class)))
                .thenReturn(transactions);
        when(balanceRepository.findByCodeAndUser_Id(ExchangerCode.WHITE_BIT, USER_ID))
                .thenReturn(Optional.of(balance));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(getDepositWithdrawalHistoryResp())
                .addHeader("Content-Type", "application/json"));

        // when
        ExchangerDepositWithdrawalTransactions result = whiteBitService.getDepositWithdrawalHistory(USER_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTransactions()).isEqualTo(transactions);
        assertThat(result.getBalanceId()).isEqualTo(BALANCE_ID);
        assertThat(result.getCode()).isEqualTo(ExchangerCode.WHITE_BIT);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
    }

    @Test
    public void shouldThrowServerException_whenWhiteBitRespCodeIs500_fromLocalEndpoint() {
        // Given

        List<DecryptedApiKeySettingDTO> decryptedKeysPair = List.of(DecryptedApiKeySettingDTO.builder()
                .publicKey(PUBLIC_KEY).privateKey(PRIVATE_KEY).code(ExchangerCode.WHITE_BIT).build());

        when(apiKeySettingRepositoryAdapter.findAllByUserId(USER_ID)).thenReturn(decryptedKeysPair);

        // Prepare the MockResponse
        String errorMessage = "Failed to get response from WhiteBit, server error";

        final MockResponse serverErrorMockResponse = new MockResponse()
                .setResponseCode(500)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(errorMessage);

        mockWebServer.enqueue(serverErrorMockResponse);
        // Preparing two responses because we expect a retry
        mockWebServer.enqueue(serverErrorMockResponse);

        // When
        Exception exception = assertThrows(WhiteBitServerException.class, () -> {
            whiteBitService.getAccountBalance(USER_ID);
        });

        // Then
        // Verifying we retried
        assertThat(mockWebServer.getRequestCount()).isEqualTo(2);
        assertThat(exception.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    public void shouldThrowServerException_whenWhiteBitRespCodeIs400_fromLocalEndpoint() {
        // Given

        List<DecryptedApiKeySettingDTO> decryptedKeysPair = List.of(DecryptedApiKeySettingDTO.builder()
                .publicKey(PUBLIC_KEY).privateKey(PRIVATE_KEY).code(ExchangerCode.WHITE_BIT).build());

        when(apiKeySettingRepositoryAdapter.findAllByUserId(USER_ID)).thenReturn(decryptedKeysPair);

        // Prepare the MockResponse
        String errorMessagePrefix = "Failed to get response from WhiteBit, client error";
        String errorMessage = "errorMessage";

        final MockResponse serverErrorMockResponse = new MockResponse()
                .setResponseCode(400)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(errorMessage);

        mockWebServer.enqueue(serverErrorMockResponse);

        // When
        Exception exception = assertThrows(WhiteBitClientException.class, () -> {
            whiteBitService.getAccountBalance(USER_ID);
        });

        // Then
        // Verifying we retried
        assertThat(mockWebServer.getRequestCount()).isEqualTo(1);
        assertThat(exception.getMessage()).isEqualTo(String.format("%s: %s", errorMessagePrefix, errorMessage));
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
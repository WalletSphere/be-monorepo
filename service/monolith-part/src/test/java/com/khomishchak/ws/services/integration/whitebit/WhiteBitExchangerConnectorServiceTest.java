package com.khomishchak.ws.services.integration.whitebit;

import com.khomishchak.ws.model.TransferTransactionType;
import com.khomishchak.ws.model.User;
import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.model.exchanger.Balance;
import com.khomishchak.ws.model.exchanger.Currency;
import com.khomishchak.ws.model.exchanger.transaction.DepositWithdrawalTransaction;
import com.khomishchak.ws.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import com.khomishchak.ws.repositories.BalanceRepository;
import com.khomishchak.ws.repositories.DepositWithdrawalTransactionsHistoryRepository;
import com.khomishchak.ws.services.integration.whitebit.mappers.WhiteBitResponseMapper;
import com.khomishchak.ws.services.integration.whitebit.model.WhiteBitBalanceResp;
import com.khomishchak.ws.services.integration.whitebit.model.WhiteBitDepositWithdrawalHistoryResp;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class WhiteBitExchangerConnectorServiceTest {

    private static final long USER_ID = 1L;
    private static final long BALANCE_ID = 2L;
    private static final long TRANSACTIONS_ID = 4L;

    @Mock
    private WhiteBitService whiteBitService;
    @Mock
    private BalanceRepository balanceRepository;
    @Mock
    private WhiteBitResponseMapper responseMapper;
    @Mock
    private DepositWithdrawalTransactionsHistoryRepository depositWithdrawalTransactionsHistoryRepository;

    private WhiteBitExchangerConnectorService connectorService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(USER_ID).build();
        connectorService = new WhiteBitExchangerConnectorService(whiteBitService, responseMapper, balanceRepository,
                depositWithdrawalTransactionsHistoryRepository);
    }

    @Test
    void shouldReturnMainBalance() {
        // given
        Currency bsv = new Currency("BSV", 1.3);
        Currency btc = new Currency("BTC", 22.11);
        Currency xlm = new Currency("XLM", 36.48);
        List<Currency> currencies = List.of(bsv, btc, xlm);

        Balance mainBalance = Balance.builder()
                        .id(BALANCE_ID)
                        .user(testUser)
                        .build();

        when(whiteBitService.getAccountBalance(USER_ID)).thenReturn(buildBalanceResponseMono());
        when(responseMapper.mapToCurrencies(any(WhiteBitBalanceResp.class))).thenReturn(currencies);
        when(balanceRepository.findByCodeAndUser_Id(ExchangerCode.WHITE_BIT, USER_ID))
                .thenReturn(Optional.of(mainBalance));


        // when
        Balance result = connectorService.getMainBalance(USER_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCurrencies()).isEqualTo(currencies);
        assertThat(result.getId()).isEqualTo(BALANCE_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
    }

    @Test
    void shouldReturnExchangerDepositWithdrawalTransactions_whenNewExchangerTransactionsIsCreated() {
        // given
        Balance balance = Balance.builder()
                .id(BALANCE_ID)
                .user(testUser)
                .build();

        List<DepositWithdrawalTransaction> depositWithdrawalTransactions = List.of(
                DepositWithdrawalTransaction.depositWithdrawalTransactionBuilder()
                        .transactionId("5e112b38-9652-11ed-a1eb-0242ac120002")
                        .transactionHash("a275a514013e4e0f927fd0d1bed215e7f6f2c4c6ce762836fe135ec22529d886")
                        .amount(BigDecimal.valueOf(0.0006))
                        .ticker("BTC")
                        .transferTransactionType(TransferTransactionType.DEPOSIT)
                        .build());

        when(whiteBitService.getDepositWithdrawalHistory(USER_ID)).thenReturn(buildDepositWithdrawalHistoryResponseMono());
        when(responseMapper.mapWithdrawalDepositHistoryToTransactions(any(WhiteBitDepositWithdrawalHistoryResp.class)))
                .thenReturn(depositWithdrawalTransactions);
        when(balanceRepository.findByCodeAndUser_Id(ExchangerCode.WHITE_BIT, USER_ID)).thenReturn(Optional.of(balance));
        // when
        ExchangerDepositWithdrawalTransactions result = connectorService.getDepositWithdrawalHistory(USER_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTransactions()).isEqualTo(depositWithdrawalTransactions);
        assertThat(result.getBalanceId()).isEqualTo(BALANCE_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
    }

    @Test
    void shouldReturnExchangerDepositWithdrawalTransactions_whenExchangerTransactionsAlreadyExists() {
        // given
        Balance balance = Balance.builder()
                .id(BALANCE_ID)
                .user(testUser)
                .build();

        ExchangerDepositWithdrawalTransactions transactions = ExchangerDepositWithdrawalTransactions.builder()
                .id(TRANSACTIONS_ID).userId(USER_ID).balance(balance).build();


        List<DepositWithdrawalTransaction> depositWithdrawalTransactions = List.of(
                DepositWithdrawalTransaction.depositWithdrawalTransactionBuilder()
                        .transactionId("5e112b38-9652-11ed-a1eb-0242ac120002")
                        .transactionHash("a275a514013e4e0f927fd0d1bed215e7f6f2c4c6ce762836fe135ec22529d886")
                        .amount(BigDecimal.valueOf(0.0006))
                        .ticker("BTC")
                        .transferTransactionType(TransferTransactionType.DEPOSIT)
                        .build());

        when(whiteBitService.getDepositWithdrawalHistory(USER_ID)).thenReturn(buildDepositWithdrawalHistoryResponseMono());
        when(responseMapper.mapWithdrawalDepositHistoryToTransactions(any(WhiteBitDepositWithdrawalHistoryResp.class)))
                .thenReturn(depositWithdrawalTransactions);
        when(balanceRepository.findByCodeAndUser_Id(ExchangerCode.WHITE_BIT, USER_ID))
                .thenReturn(Optional.of(balance));
        when(depositWithdrawalTransactionsHistoryRepository.findByBalance_Id(BALANCE_ID))
                .thenReturn(Optional.of(transactions));
        // when
        ExchangerDepositWithdrawalTransactions result = connectorService.getDepositWithdrawalHistory(USER_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTransactions()).isEqualTo(depositWithdrawalTransactions);
        assertThat(result.getBalanceId()).isEqualTo(BALANCE_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
    }

    Mono<WhiteBitBalanceResp> buildBalanceResponseMono() {
        return Mono.just(ClientResponse.create(HttpStatus.valueOf(200))
                .header("Content-Type", "application/json")
                .body(getBalanceRespJson())
                .build())
                .flatMap(auth -> auth.bodyToMono(WhiteBitBalanceResp.class));
    }

    Mono<WhiteBitDepositWithdrawalHistoryResp> buildDepositWithdrawalHistoryResponseMono() {
        return Mono.just(ClientResponse.create(HttpStatus.valueOf(200))
                        .header("Content-Type", "application/json")
                        .body(getDepositWithdrawalHistoryResp())
                        .build())
                .flatMap(auth -> auth.bodyToMono(WhiteBitDepositWithdrawalHistoryResp.class));
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
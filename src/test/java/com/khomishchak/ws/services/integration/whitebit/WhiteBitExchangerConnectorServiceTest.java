package com.khomishchak.ws.services.integration.whitebit;

import com.khomishchak.ws.model.exchanger.Balance;
import com.khomishchak.ws.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WhiteBitExchangerConnectorServiceTest {

    private static final long USER_ID = 1L;
    private static final long BALANCE_ID = 2L;
    private static final long TRANSACTIONS_ID = 4L;

    @Mock
    private WhiteBitService whiteBitService;

    private WhiteBitExchangerConnectorService connectorService;

    @BeforeEach
    void setUp() {
        connectorService = new WhiteBitExchangerConnectorService(whiteBitService);
    }

    @Test
    void shouldReturnMainBalance() {
        // given
        Balance mainBalance = Balance.builder()
                        .id(BALANCE_ID)
                        .build();
        when(whiteBitService.getAccountBalance(USER_ID)).thenReturn(mainBalance);

        // when
        Balance result = connectorService.getMainBalance(USER_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(mainBalance);
    }

    @Test
    void shouldReturnExchangerDepositWithdrawalTransactions() {
        // given
        ExchangerDepositWithdrawalTransactions transactions = ExchangerDepositWithdrawalTransactions.builder()
                .id(TRANSACTIONS_ID)
                .build();

        when(whiteBitService.getDepositWithdrawalHistory(USER_ID)).thenReturn(transactions);

        // when
        ExchangerDepositWithdrawalTransactions result = connectorService.getDepositWithdrawalHistory(USER_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(transactions);
    }
}
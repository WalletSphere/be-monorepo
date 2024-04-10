package com.khomishchak.ws.services.exchangers.balances.cache;

import com.khomishchak.ws.services.exchangers.balances.history.AccountBalanceTransferOperationsHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceCacheHandlerTest {

    private static final long BALANCE_ID = 2L;

    @Mock
    private AccountBalanceTransferOperationsHistoryService accountBalanceTransferOperationsHistoryService;

    BalanceCacheHandler balanceCacheHandler;

    @BeforeEach
    void setUp() {
        balanceCacheHandler = new BalanceCacheHandler(accountBalanceTransferOperationsHistoryService);
    }

    @Test
    void deleteAllBalanceRelatedCacheInfo() {
        // given
        doNothing().when(accountBalanceTransferOperationsHistoryService)
                .deleteDepositWithdrawalTransactionsHistory(eq(BALANCE_ID));

        // when
        balanceCacheHandler.deleteAllBalanceRelatedCacheInfo(BALANCE_ID);

        // then
        verify(accountBalanceTransferOperationsHistoryService, times(1))
                .deleteDepositWithdrawalTransactionsHistory(eq(BALANCE_ID));
    }
}
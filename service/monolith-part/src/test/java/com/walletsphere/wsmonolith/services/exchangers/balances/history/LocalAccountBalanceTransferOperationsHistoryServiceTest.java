package com.walletsphere.wsmonolith.services.exchangers.balances.history;

import com.walletsphere.wsmonolith.model.enums.ExchangerCode;
import com.walletsphere.wsmonolith.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import com.walletsphere.wsmonolith.repositories.DepositWithdrawalTransactionsHistoryRepository;
import com.walletsphere.wsmonolith.services.UserService;
import com.walletsphere.wsmonolith.services.exchangers.ExchangerConnectorService;
import com.walletsphere.wsmonolith.services.exchangers.ExchangerConnectorServiceFactory;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class LocalAccountBalanceTransferOperationsHistoryServiceTest {

    private static final long USER_ID = 1L;
    private static final long TRANSACTIONS_ID = 1L;

    @Mock
    private UserService userService;
    @Mock
    private ExchangerConnectorServiceFactory exchangerServiceFactory;
    @Mock
    private ExchangerConnectorService exchangerConnectorService;
    @Mock
    private DepositWithdrawalTransactionsHistoryRepository depositWithdrawalTransactionsHistoryRepository;

    private LocalAccountBalanceTransferOperationsHistoryService localAccountBalanceTransferOperationsHistoryService;

    @BeforeEach
    void setUp() {
        when(exchangerServiceFactory.getExchangerCode()).thenReturn(ExchangerCode.WHITE_BIT);
        localAccountBalanceTransferOperationsHistoryService = new LocalAccountBalanceTransferOperationsHistoryService(
                userService, List.of(exchangerServiceFactory), depositWithdrawalTransactionsHistoryRepository);

    }

    @Test
    void shouldReturnDepositWithdrawalTransactionsHistory() {
        // given
        List<ExchangerDepositWithdrawalTransactions> transactions = List.of(ExchangerDepositWithdrawalTransactions.builder()
                .id(TRANSACTIONS_ID).build());
        when(depositWithdrawalTransactionsHistoryRepository.findAllByUserId(USER_ID)).thenReturn(transactions);

        // when
        List<ExchangerDepositWithdrawalTransactions> result =
                localAccountBalanceTransferOperationsHistoryService.getDepositWithdrawalTransactionsHistory(USER_ID);

        // then
        assertThat(result).isEqualTo(transactions);
    }
}
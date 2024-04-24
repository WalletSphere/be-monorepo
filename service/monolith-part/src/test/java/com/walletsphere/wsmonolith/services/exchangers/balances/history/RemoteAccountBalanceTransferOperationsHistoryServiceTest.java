package com.walletsphere.wsmonolith.services.exchangers.balances.history;

import com.walletsphere.wsmonolith.model.User;
import com.walletsphere.wsmonolith.model.enums.ExchangerCode;
import com.walletsphere.wsmonolith.model.exchanger.ApiKeySetting;
import com.walletsphere.wsmonolith.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import com.walletsphere.wsmonolith.repositories.DepositWithdrawalTransactionsHistoryRepository;
import com.walletsphere.wsmonolith.services.UserService;
import com.walletsphere.wsmonolith.services.exchangers.ExchangerConnectorService;
import com.walletsphere.wsmonolith.services.exchangers.ExchangerConnectorServiceFactory;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class RemoteAccountBalanceTransferOperationsHistoryServiceTest {

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

    private RemoteAccountBalanceTransferOperationsHistoryService remoteAccountBalanceTransferOperationsHistoryService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(USER_ID).build();
        when(exchangerServiceFactory.getExchangerCode()).thenReturn(ExchangerCode.WHITE_BIT);
        remoteAccountBalanceTransferOperationsHistoryService =
                new RemoteAccountBalanceTransferOperationsHistoryService(userService,
                        List.of(exchangerServiceFactory), depositWithdrawalTransactionsHistoryRepository);
    }

    @Test
    void shouldReturnTheSameTransactionsListAsSyncMethod() {
        //given
        ApiKeySetting apiKeySetting = ApiKeySetting.builder()
                .code(ExchangerCode.WHITE_BIT)
                .build();

        testUser.setApiKeysSettings(List.of(apiKeySetting));

        ExchangerDepositWithdrawalTransactions transactions = ExchangerDepositWithdrawalTransactions.builder()
                .id(TRANSACTIONS_ID).build();

        when(userService.getUserById(USER_ID)).thenReturn(testUser);
        when(exchangerServiceFactory.newInstance()).thenReturn(exchangerConnectorService);
        when(exchangerConnectorService.getDepositWithdrawalHistory(eq(USER_ID))).thenReturn(transactions);
        when(depositWithdrawalTransactionsHistoryRepository.saveAll(eq(List.of(transactions))))
                .thenReturn(List.of(transactions));

        // when
        List<ExchangerDepositWithdrawalTransactions> result
                = remoteAccountBalanceTransferOperationsHistoryService.getDepositWithdrawalTransactionsHistory(USER_ID);

        // then
        assertThat(result).isEqualTo(List.of(transactions));
    }
}
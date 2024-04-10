package com.khomishchak.ws.services.exchangers.balances.history;

import com.khomishchak.ws.model.User;
import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.model.exchanger.ApiKeySetting;
import com.khomishchak.ws.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import com.khomishchak.ws.repositories.DepositWithdrawalTransactionsHistoryRepository;
import com.khomishchak.ws.services.UserService;
import com.khomishchak.ws.services.exchangers.ExchangerConnectorService;
import com.khomishchak.ws.services.exchangers.ExchangerConnectorServiceFactory;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CommonAccountBalanceTransferOperationsHistoryServiceTest {

    private static final long USER_ID = 1L;
    private static final long BALANCE_ID = 2L;
    private static final long TRANSACTIONS_ID = 4L;

    @Mock
    private UserService userService;
    @Mock
    private ExchangerConnectorServiceFactory exchangerServiceFactory;
    @Mock
    private ExchangerConnectorService exchangerConnectorService;
    @Mock
    private DepositWithdrawalTransactionsHistoryRepository depositWithdrawalTransactionsHistoryRepository;

    private CommonAccountBalanceTransferOperationsHistoryService commonAccountBalanceTransferOperationsHistoryService;

    private User testUser;

    @BeforeEach
    void setUp() {
        when(exchangerServiceFactory.getExchangerCode()).thenReturn(ExchangerCode.WHITE_BIT);
        testUser = User.builder().id(USER_ID).build();
        commonAccountBalanceTransferOperationsHistoryService =
                mock(CommonAccountBalanceTransferOperationsHistoryService.class, withSettings()
                .useConstructor(userService, List.of(exchangerServiceFactory), depositWithdrawalTransactionsHistoryRepository)
                .defaultAnswer(CALLS_REAL_METHODS));
    }

    @Test
    void shouldReturnSynchronizeDepositWithdrawalTransactionsHistory () {
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
                = commonAccountBalanceTransferOperationsHistoryService.synchronizeDepositWithdrawalTransactionsHistory(USER_ID);

        // then
        assertThat(result).isEqualTo(List.of(transactions));
    }

    @Test
    void shouldDeleteDepositWithdrawalTransactionsHistory () {
        //given

        // when
        commonAccountBalanceTransferOperationsHistoryService.deleteDepositWithdrawalTransactionsHistory(BALANCE_ID);

        // then
        verify(depositWithdrawalTransactionsHistoryRepository, times(1)).deleteAllByBalance_Id(BALANCE_ID);
    }
}
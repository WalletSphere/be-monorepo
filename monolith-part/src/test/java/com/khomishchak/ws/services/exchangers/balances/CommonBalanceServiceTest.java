package com.khomishchak.ws.services.exchangers.balances;

import com.khomishchak.ws.model.User;
import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.model.exchanger.ApiKeySetting;
import com.khomishchak.ws.model.exchanger.Balance;
import com.khomishchak.ws.repositories.BalanceRepository;
import com.khomishchak.ws.services.UserService;
import com.khomishchak.ws.services.exchangers.ExchangerConnectorService;
import com.khomishchak.ws.services.exchangers.ExchangerConnectorServiceFactory;
import com.khomishchak.ws.services.exchangers.balances.cache.BalanceCacheHandler;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CommonBalanceServiceTest {

    private static final String BALANCE_NAME = "balanceName";
    private static final long USER_ID = 1L;
    private static final long BALANCE_ID = 2L;

    @Mock
    private BalanceRepository balanceRepository;
    @Mock
    private UserService userService;
    @Mock
    private ExchangerConnectorServiceFactory exchangerServiceFactory;
    @Mock
    private ExchangerConnectorService exchangerConnectorService;
    @Mock
    private BalanceCacheHandler balanceCacheHandler;

    private CommonBalanceService commonBalanceService;

    private User testUser;

    @BeforeEach
    void setUp() {
        when(exchangerServiceFactory.getExchangerCode()).thenReturn(ExchangerCode.WHITE_BIT);
        testUser = User.builder().id(USER_ID).build();
        commonBalanceService = mock(CommonBalanceService.class, withSettings()
                .useConstructor(balanceRepository, userService, List.of(exchangerServiceFactory), balanceCacheHandler)
                .defaultAnswer(CALLS_REAL_METHODS));
    }

    @Test
    void shouldReturnEmptyBalanceThatWasPersisted() {
        // given
        ExchangerCode code = ExchangerCode.WHITE_BIT;

        Balance expectedBalance = Balance.builder()
                        .code(code)
                        .balanceName(BALANCE_NAME)
                        .user(testUser)
                        .build();

        when(balanceRepository.save(eq(expectedBalance))).thenReturn(expectedBalance.toBuilder().id(BALANCE_ID).build());


        // when
        Balance result = commonBalanceService.registerBalanceEntryInfo(code, BALANCE_NAME, testUser);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getBalanceName()).isEqualTo(BALANCE_NAME);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getCurrencies()).isNull();
        assertThat(result.getDepositWithdrawalTransactions()).isNull();
        assertThat(result.getLastTimeWasUpdated()).isNull();
    }

    @Test
    void shouldReturnSynchronizedBalances() {
        // given
        ApiKeySetting apiKeySetting = ApiKeySetting.builder()
                .code(ExchangerCode.WHITE_BIT)
                .build();

        testUser.setApiKeysSettings(List.of(apiKeySetting));
        Balance mainBalance = Balance.builder().id(BALANCE_ID).build();

        when(userService.getUserById(eq(USER_ID))).thenReturn(testUser);
        when(exchangerServiceFactory.newInstance()).thenReturn(exchangerConnectorService);
        when(exchangerConnectorService.getMainBalance(eq(USER_ID))).thenReturn(mainBalance);
        when(balanceRepository.saveAll(eq(List.of(mainBalance)))).thenReturn(List.of(mainBalance));

        // when
        List<Balance> balances = commonBalanceService.synchronizeBalances(USER_ID);

        // then
        assertThat(balances).isEqualTo(List.of(mainBalance));
    }

    @Test
    void shouldDeleteBalanceById() {
        // given
        doNothing().when(balanceRepository).deleteById(BALANCE_ID);
        doNothing().when(balanceCacheHandler).deleteAllBalanceRelatedCacheInfo(BALANCE_ID);

        // when
        commonBalanceService.deleteBalance(BALANCE_ID);

        // then
        verify(balanceRepository, times(1)).deleteById(BALANCE_ID);
        verify(balanceCacheHandler, times(1)).deleteAllBalanceRelatedCacheInfo(BALANCE_ID);
    }

    @Test
    void shouldReturnBalanceByCodeAndUserId_whenIsPresent() {
        // given
        ExchangerCode code = ExchangerCode.WHITE_BIT;
        Balance balance = Balance.builder().id(BALANCE_ID).build();

        when(balanceRepository.findByCodeAndUser_Id(code, USER_ID)).thenReturn(Optional.of(balance));

        // when
        Balance result = commonBalanceService.getBalanceByCodeAndUserIdOrThrow(USER_ID, code);

        // then
        assertThat(result).isEqualTo(balance);
    }

}
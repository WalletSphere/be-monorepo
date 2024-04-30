package com.walletsphere.wsmonolith.services.exchangers.balances;

import com.walletsphere.wsmonolith.model.User;
import com.walletsphere.wsmonolith.model.enums.ExchangerCode;
import com.walletsphere.wsmonolith.model.exchanger.ApiKeySetting;
import com.walletsphere.wsmonolith.model.exchanger.Balance;
import com.walletsphere.wsmonolith.repositories.BalanceRepository;
import com.walletsphere.wsmonolith.services.UserService;
import com.walletsphere.wsmonolith.services.exchangers.ExchangerConnectorService;
import com.walletsphere.wsmonolith.services.exchangers.ExchangerConnectorServiceFactory;
import com.walletsphere.wsmonolith.services.exchangers.apikeys.ApiKeySettingService;
import com.walletsphere.wsmonolith.services.exchangers.balances.cache.BalanceCacheHandler;
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
class RemoteBalanceServiceTest {

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
    @Mock
    private ApiKeySettingService apiKeySettingService;

    private RemoteBalanceService remoteBalanceService;

    private User testUser;

    @BeforeEach
    void setUp() {
        when(exchangerServiceFactory.getExchangerCode()).thenReturn(ExchangerCode.WHITE_BIT);
        testUser = User.builder().id(USER_ID).build();
        remoteBalanceService = new RemoteBalanceService(balanceRepository, List.of(exchangerServiceFactory),
                userService , balanceCacheHandler, apiKeySettingService);
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
        List<Balance> balances = remoteBalanceService.synchronizeBalances(USER_ID);

        // then
        assertThat(balances).isEqualTo(List.of(mainBalance));
    }

    @Test
    void shouldReturnSynchronizedBalance() {
        // given
        ExchangerCode code = ExchangerCode.WHITE_BIT;
        Balance mainBalance = Balance.builder().id(BALANCE_ID).build();

        when(exchangerServiceFactory.newInstance()).thenReturn(exchangerConnectorService);
        when(exchangerConnectorService.getMainBalance(USER_ID)).thenReturn(mainBalance);

        // when
        Balance result = remoteBalanceService.getMainBalance(USER_ID, code);

        // then
        assertThat(result).isEqualTo(mainBalance);
    }
}
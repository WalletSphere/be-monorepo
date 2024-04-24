package com.walletsphere.wsmonolith.services.exchangers.balances;

import com.walletsphere.wsmonolith.model.enums.ExchangerCode;
import com.walletsphere.wsmonolith.model.exchanger.Balance;
import com.walletsphere.wsmonolith.repositories.BalanceRepository;
import com.walletsphere.wsmonolith.services.UserService;
import com.walletsphere.wsmonolith.services.exchangers.ExchangerConnectorServiceFactory;
import com.walletsphere.wsmonolith.services.exchangers.apikeys.ApiKeySettingService;
import com.walletsphere.wsmonolith.services.exchangers.balances.cache.BalanceCacheHandler;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class LocalBalanceServiceTest {

    private static final long USER_ID = 1L;

    @Mock
    private BalanceRepository balanceRepository;
    @Mock
    private UserService userService;
    @Mock
    private ExchangerConnectorServiceFactory exchangerServiceFactory;
    @Mock
    private BalanceCacheHandler balanceCacheHandler;

    @Mock
    private ApiKeySettingService apiKeySettingService;

    private LocalBalanceService localBalanceService;

    @BeforeEach
    void setUp() {
        localBalanceService = new LocalBalanceService(balanceRepository, userService, List.of(exchangerServiceFactory),
                balanceCacheHandler, apiKeySettingService);
    }

    @Test
    void shouldReturnMainBalancesFromBalanceRepository_byUserId() {
        // given
        Balance balance1 = Balance.builder().id(2L).build();
        Balance balance2 = Balance.builder().id(3L).build();

        List<Balance> balances = List.of(balance1, balance2);

        when(balanceRepository.findAllByUser_Id(USER_ID)).thenReturn(balances);

        // when
        List<Balance> result = localBalanceService.getMainBalances(USER_ID);

        // then
        assertThat(result).isEqualTo(balances);
    }

    @Test
    void shouldReturnMainBalanceFromBalanceRepository_byUserIdAndCode() {
        // given
        ExchangerCode code = ExchangerCode.WHITE_BIT;
        Balance mainBalance = Balance.builder().id(2L).build();

        when(balanceRepository.findByCodeAndUser_Id(code, USER_ID)).thenReturn(Optional.of(mainBalance));

        // when
        Balance result = localBalanceService.getMainBalance(USER_ID, code);

        // then
        assertThat(result).isEqualTo(mainBalance);
    }
}
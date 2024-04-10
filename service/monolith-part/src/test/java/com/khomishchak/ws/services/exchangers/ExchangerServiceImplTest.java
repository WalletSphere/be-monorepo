package com.khomishchak.ws.services.exchangers;

import com.khomishchak.ws.model.User;
import com.khomishchak.ws.model.enums.ExchangerCode;
import com.khomishchak.ws.model.enums.RegistrationStatus;
import com.khomishchak.ws.model.exchanger.ApiKeySetting;
import com.khomishchak.ws.model.exchanger.Balance;
import com.khomishchak.ws.model.exchanger.transaction.ExchangerDepositWithdrawalTransactions;
import com.khomishchak.ws.model.requests.RegisterApiKeysReq;
import com.khomishchak.ws.model.requests.RegisterExchangerInfoReq;
import com.khomishchak.ws.model.response.FirstlyGeneratedBalanceResp;
import com.khomishchak.ws.repositories.ApiKeySettingRepository;
import com.khomishchak.ws.services.UserService;
import com.khomishchak.ws.services.exchangers.balances.BalanceService;
import com.khomishchak.ws.services.exchangers.balances.history.AccountBalanceTransferOperationsHistoryService;
import com.khomishchak.ws.services.security.encryption.AesEncryptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangerServiceImplTest {

    private static final long USER_ID = 1L;
    private static final long BALANCE_ID = 2L;
    private static final ExchangerCode CODE = ExchangerCode.WHITE_BIT;

    @Mock
    private UserService userService;
    @Mock
    private ApiKeySettingRepository apiKeySettingRepository;
    @Mock
    private AesEncryptionService aesEncryptionService;
    @Mock
    private BalanceService balanceService;
    @Mock
    private AccountBalanceTransferOperationsHistoryService accountBalanceTransferOperationsHistoryService;

    private ExchangerService exchangerService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        exchangerService = new ExchangerServiceImpl(userService, apiKeySettingRepository, aesEncryptionService,
                balanceService, accountBalanceTransferOperationsHistoryService);
    }

    @Test
    void shouldCreateGeneralExchangerInfo() {
        // given
        testUser.setApiKeysSettings(new ArrayList<>());

        String balanceName = "balanceName";
        String publicKey = "publicKey";
        String secretKey = "secretKey";

        Balance emptyBalance = Balance.builder()
                .id(BALANCE_ID)
                .code(CODE)
                .balanceName(balanceName)
                .user(testUser)
                .build();

        RegisterApiKeysReq apiKeysReq = new RegisterApiKeysReq(publicKey, secretKey);

        RegisterExchangerInfoReq infoReq = new RegisterExchangerInfoReq(apiKeysReq, CODE, balanceName);

        when(userService.getUserById(eq(USER_ID))).thenReturn(testUser);
        when(aesEncryptionService.encrypt(anyString())).thenReturn("encryptedData");
        when(apiKeySettingRepository.save(any(ApiKeySetting.class))).thenReturn(new ApiKeySetting());
        when(balanceService.registerBalanceEntryInfo(eq(CODE), eq(balanceName), eq(testUser))).thenReturn(emptyBalance);

        // when
        FirstlyGeneratedBalanceResp result = exchangerService.addGeneralExchangerInfo(infoReq, USER_ID);

        // then
        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.balanceId()).isEqualTo(BALANCE_ID);
        assertThat(result.status()).isEqualTo(RegistrationStatus.SUCCESSFUL);
    }

    @Test
    void shouldReturnMainBalance() {
        // given
        Balance mainBalance = Balance.builder()
                .id(BALANCE_ID)
                .build();

        when(balanceService.getMainBalance(eq(USER_ID), eq(CODE))).thenReturn(mainBalance);

        // when
        Balance result = exchangerService.getMainBalance(USER_ID, CODE);

        // then
        assertThat(result).isEqualTo(mainBalance);
    }

    @Test
    void shouldReturnAllMainBalances() {
        // given
        Balance mainBalance = Balance.builder()
                .id(BALANCE_ID)
                .build();

        when(balanceService.getMainBalances(eq(USER_ID))).thenReturn(List.of(mainBalance));

        // when
        List<Balance> result = exchangerService.getAllMainBalances(USER_ID);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(mainBalance);
    }

    @Test
    void shouldReturnWithdrawalDepositWalletHistory() {
        // given
        ExchangerDepositWithdrawalTransactions transactions = ExchangerDepositWithdrawalTransactions.builder()
                .id(3L).build();

        when(accountBalanceTransferOperationsHistoryService.getDepositWithdrawalTransactionsHistory(eq(USER_ID)))
                .thenReturn(List.of(transactions));

        // when
        List<ExchangerDepositWithdrawalTransactions> result = exchangerService.getWithdrawalDepositWalletHistory(USER_ID);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(transactions);
    }

    @Test
    void shouldReturnSynchronizeDepositWithdrawalTransactionsResponse() {
        // given
        Balance firstSyncBalance = Balance.builder().id(BALANCE_ID).build();
        Balance secondSyncBalance = Balance.builder().id(BALANCE_ID).build();

        when(balanceService.synchronizeBalances(eq(USER_ID))).thenReturn(List.of(firstSyncBalance, secondSyncBalance));

        // when
        List<Balance> resultBalances = exchangerService.synchronizeBalanceDataForUser(USER_ID);

        // then
        assertThat(resultBalances.size()).isEqualTo(2);
        assertThat(resultBalances.get(0)).isEqualTo(firstSyncBalance);
        assertThat(resultBalances.get(1)).isEqualTo(secondSyncBalance);
    }

    @Test
    void shouldReturnSynchronizeBalancesResponse() {
        // given
        ExchangerDepositWithdrawalTransactions transactions = ExchangerDepositWithdrawalTransactions.builder()
                .id(3L).build();

        when(accountBalanceTransferOperationsHistoryService.synchronizeDepositWithdrawalTransactionsHistory(eq(USER_ID)))
                .thenReturn(List.of(transactions));

        // when
        List<ExchangerDepositWithdrawalTransactions> result =
                exchangerService.synchronizeDepositWithdrawalTransactionsData(USER_ID);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(transactions);
    }

    @Test
    void shouldDeleteExchanger() {
        // given
        doNothing().when(balanceService).deleteBalance(eq(BALANCE_ID));

        // when
        exchangerService.deleteExchangerForUser(BALANCE_ID);

        // then
        verify(balanceService, times(1)).deleteBalance(eq(BALANCE_ID));
    }
}
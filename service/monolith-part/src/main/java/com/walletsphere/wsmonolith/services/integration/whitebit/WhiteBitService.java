package com.walletsphere.wsmonolith.services.integration.whitebit;

import com.walletsphere.wsmonolith.services.integration.whitebit.model.WhiteBitBalanceResp;
import com.walletsphere.wsmonolith.services.integration.whitebit.model.WhiteBitDepositWithdrawalHistoryResp;
import reactor.core.publisher.Mono;

public interface WhiteBitService {

    Mono<WhiteBitBalanceResp> getAccountBalance(long userId);

    Mono<WhiteBitDepositWithdrawalHistoryResp> getDepositWithdrawalHistory(long userId);
}

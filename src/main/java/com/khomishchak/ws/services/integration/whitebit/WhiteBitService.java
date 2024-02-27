package com.khomishchak.ws.services.integration.whitebit;

import com.khomishchak.ws.services.integration.whitebit.model.WhiteBitBalanceResp;
import com.khomishchak.ws.services.integration.whitebit.model.WhiteBitDepositWithdrawalHistoryResp;
import reactor.core.publisher.Mono;

public interface WhiteBitService {

    Mono<WhiteBitBalanceResp> getAccountBalance(long userId);

    Mono<WhiteBitDepositWithdrawalHistoryResp> getDepositWithdrawalHistory(long userId);
}

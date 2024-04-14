package com.walletsphere.wsmonolith.model.response;

import com.walletsphere.wsmonolith.model.enums.RegistrationStatus;

public record FirstlyGeneratedBalanceResp (Long balanceId, Long userId, RegistrationStatus status){
}

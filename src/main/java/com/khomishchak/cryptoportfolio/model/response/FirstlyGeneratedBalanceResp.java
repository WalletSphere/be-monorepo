package com.khomishchak.cryptoportfolio.model.response;

import com.khomishchak.cryptoportfolio.model.enums.RegistrationStatus;

public record FirstlyGeneratedBalanceResp (Long balanceId, Long userId, RegistrationStatus status){
}

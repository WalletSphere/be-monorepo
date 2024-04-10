package com.khomishchak.ws.model.response;

import com.khomishchak.ws.model.enums.RegistrationStatus;

public record FirstlyGeneratedBalanceResp (Long balanceId, Long userId, RegistrationStatus status){
}

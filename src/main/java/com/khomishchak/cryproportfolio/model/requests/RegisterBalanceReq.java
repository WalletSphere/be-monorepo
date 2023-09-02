package com.khomishchak.cryproportfolio.model.requests;

import com.khomishchak.cryproportfolio.model.enums.ExchangerCode;

public record RegisterBalanceReq(String publicKey, String secretKey, ExchangerCode code) {
}

package com.khomishchak.cryptoportfolio.model.requests;

import com.khomishchak.cryptoportfolio.model.enums.ExchangerCode;

public record RegisterBalanceReq(String publicKey, String secretKey, ExchangerCode code) {
}

package com.khomishchak.cryptoportfolio.model.requests;

import com.khomishchak.cryptoportfolio.model.enums.ExchangerCode;

public record RegisterApiKeysReq(String publicKey, String secretKey, ExchangerCode code) {
}

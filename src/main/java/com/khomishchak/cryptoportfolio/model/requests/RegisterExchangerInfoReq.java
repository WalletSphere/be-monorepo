package com.khomishchak.cryptoportfolio.model.requests;

import com.khomishchak.cryptoportfolio.model.enums.ExchangerCode;

public record RegisterExchangerInfoReq(RegisterApiKeysReq apiKeysReq, ExchangerCode code, String balanceName) {
}

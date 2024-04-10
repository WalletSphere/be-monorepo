package com.khomishchak.ws.model.requests;

import com.khomishchak.ws.model.enums.ExchangerCode;

public record RegisterExchangerInfoReq(RegisterApiKeysReq apiKeysReq, ExchangerCode code, String balanceName) {
}

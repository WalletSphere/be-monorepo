package com.walletsphere.wsmonolith.model.requests;

import com.walletsphere.wsmonolith.model.enums.ExchangerCode;

public record RegisterExchangerInfoReq(RegisterApiKeysReq apiKeysReq, ExchangerCode code, String balanceName) {
}

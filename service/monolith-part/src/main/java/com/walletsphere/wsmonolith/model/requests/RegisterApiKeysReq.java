package com.walletsphere.wsmonolith.model.requests;

import com.walletsphere.wsmonolith.model.enums.ExchangerCode;

public record RegisterApiKeysReq(String publicKey, String secretKey, ExchangerCode code) {
}

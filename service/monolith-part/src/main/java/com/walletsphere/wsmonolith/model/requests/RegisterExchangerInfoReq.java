package com.walletsphere.wsmonolith.model.requests;

public record RegisterExchangerInfoReq(RegisterApiKeysReq apiKeysReq, String balanceName) {
}

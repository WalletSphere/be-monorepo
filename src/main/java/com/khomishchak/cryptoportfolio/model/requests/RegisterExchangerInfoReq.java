package com.khomishchak.cryptoportfolio.model.requests;

public record RegisterExchangerInfoReq(RegisterApiKeysReq apiKeysReq, String balanceName) {
}

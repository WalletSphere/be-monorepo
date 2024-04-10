package com.khomishchak.authservice.model.auth.resp;

public record ProcessedTokenResp(Long userId, boolean validated) {
}

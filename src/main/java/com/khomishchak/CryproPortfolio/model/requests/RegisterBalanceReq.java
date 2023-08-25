package com.khomishchak.CryproPortfolio.model.requests;

import com.khomishchak.CryproPortfolio.model.enums.ExchangerCode;

public record RegisterBalanceReq(String publicKey, String secretKey, ExchangerCode code) {
}

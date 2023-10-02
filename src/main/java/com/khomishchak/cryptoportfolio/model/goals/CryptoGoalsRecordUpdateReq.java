package com.khomishchak.cryptoportfolio.model.goals;

import java.math.BigDecimal;

public record CryptoGoalsRecordUpdateReq(String ticker, double amount, BigDecimal price) {
}

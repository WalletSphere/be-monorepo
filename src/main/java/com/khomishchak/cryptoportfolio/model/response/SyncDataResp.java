package com.khomishchak.cryptoportfolio.model.response;

import com.khomishchak.cryptoportfolio.model.exchanger.Balance;

import java.util.List;

public record SyncDataResp(List<Balance> synchronizedBalances) {
}

package com.khomishchak.cryptoportfolio.services.integration.whitebit.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class WhiteBitBalanceResp {

    private Map<String, String> currencies = new HashMap<>();

    @JsonAnySetter
    public void setCurrencies(String name, Map<String, String> currencies) {
        this.currencies.put(name, currencies.get("main_balance"));
    }

    public Map<String, String> getCurrencies() {
        return currencies;
    }
}

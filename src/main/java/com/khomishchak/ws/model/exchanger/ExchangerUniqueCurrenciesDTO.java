package com.khomishchak.ws.model.exchanger;

import com.khomishchak.ws.model.enums.ExchangerCode;

public record ExchangerUniqueCurrenciesDTO(ExchangerCode code, Object currencies) {
}

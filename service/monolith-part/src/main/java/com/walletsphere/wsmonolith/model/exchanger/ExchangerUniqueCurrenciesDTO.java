package com.walletsphere.wsmonolith.model.exchanger;

import com.walletsphere.wsmonolith.model.enums.ExchangerCode;

public record ExchangerUniqueCurrenciesDTO(ExchangerCode code, Object currencies) {
}

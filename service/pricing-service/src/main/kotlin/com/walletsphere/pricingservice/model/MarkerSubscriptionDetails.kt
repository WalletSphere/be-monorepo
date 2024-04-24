package com.walletsphere.pricingservice.model

import com.walletsphere.pricingservice.model.integration.CryptoExchanger



data class MarkerSubscriptionDetails(val exchanger: CryptoExchanger,
                                     val initialCurrency: String,
                                     val tickers: List<String>) {
    val allTickers : List<String> = listOf(initialCurrency) + tickers
}

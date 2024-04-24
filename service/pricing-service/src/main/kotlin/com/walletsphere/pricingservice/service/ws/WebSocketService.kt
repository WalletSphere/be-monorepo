package com.walletsphere.pricingservice.service.ws

import com.walletsphere.pricingservice.model.MarkerSubscriptionDetails
import com.walletsphere.pricingservice.model.integration.CryptoExchanger

interface WebSocketService {
    fun subscribe(accountId: Long, subscriptionDetails: com.walletsphere.pricingservice.model.MarkerSubscriptionDetails)
    fun getLastPrices(exchanger: CryptoExchanger, tickers: List<String>): Map<String, Double>
}
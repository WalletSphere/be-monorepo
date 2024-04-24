package com.walletsphere.pricingservice.service.integration

import okhttp3.OkHttpClient
import com.walletsphere.pricingservice.model.integration.CryptoExchanger


interface IntegrationWebSocketService {
    fun getCryptoExchangerType(): CryptoExchanger
    fun connect(client: OkHttpClient)
    fun subscribe(accountId: Long, subscriptionDetails: com.walletsphere.pricingservice.model.MarkerSubscriptionDetails)
    fun getLastPrices(tickers: List<String>): Map<String, Double>
    fun subscribeToAlreadyFollowedTickers(currencies: List<String>)
}
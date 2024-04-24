package com.walletsphere.pricingservice.service.ws

import com.google.gson.Gson
import com.walletsphere.pricingservice.model.GET_USED_CURRENCIES_URL
import com.walletsphere.pricingservice.model.MarkerSubscriptionDetails
import com.walletsphere.pricingservice.model.integration.CryptoExchanger
import com.walletsphere.pricingservice.model.internall.exchanger.UsedToken
import com.walletsphere.pricingservice.model.internall.exchanger.UsedTokens
import com.walletsphere.pricingservice.service.integration.IntegrationWebSocketService
import com.walletsphere.pricingservice.utility.mapJsonResp
import okhttp3.OkHttpClient
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@Service
class WebSocketServiceImpl (private val integrationWebSocketConnectors: List<IntegrationWebSocketService>,
                            private val restTemplate: RestTemplate)
    : WebSocketService {

    private val client = OkHttpClient()

    private val websocketIntegrations: Map<CryptoExchanger, IntegrationWebSocketService> =
            integrationWebSocketConnectors.associateBy { it.getCryptoExchangerType() }

    init {
        subscribeToAlreadyFollowedTickers()
        integrationWebSocketConnectors.forEach { it.connect(client) }
    }

    override fun subscribe(accountId: Long, subscriptionDetails: com.walletsphere.pricingservice.model.MarkerSubscriptionDetails) =
            websocketIntegrations[subscriptionDetails.exchanger]?.subscribe(accountId, subscriptionDetails) ?: Unit

    override fun getLastPrices(exchanger: CryptoExchanger, tickers: List<String>) =
            websocketIntegrations[exchanger]?.getLastPrices(tickers) ?: emptyMap();

    private fun subscribeToAlreadyFollowedTickers() {
        val response = restTemplate.getForObject<String>(GET_USED_CURRENCIES_URL)
        val data = Gson().mapJsonResp<List<UsedToken>>(response)

        UsedTokens(data).records.forEach {
            websocketIntegrations[CryptoExchanger.valueOf(it.code)]
                    ?.subscribeToAlreadyFollowedTickers(it.currencies.split(", ").toList())
        }
    }
}
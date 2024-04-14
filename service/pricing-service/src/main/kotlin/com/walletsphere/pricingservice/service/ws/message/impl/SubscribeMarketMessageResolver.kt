package com.walletsphere.pricingservice.service.ws.message.impl

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.walletsphere.pricingservice.model.MarkerSubscriptionDetails
import com.walletsphere.pricingservice.model.integration.CryptoExchanger
import com.walletsphere.pricingservice.service.ws.SessionMappingService
import com.walletsphere.pricingservice.service.ws.WebSocketService
import com.walletsphere.pricingservice.service.ws.message.WsMessageResolver
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession

fun JsonObject.mapToMarkerSubscriptionDetails() =
        com.walletsphere.pricingservice.model.MarkerSubscriptionDetails(
                CryptoExchanger.valueOf(this["exchanger"].asString),
                this["initialCurrency"].asString,
                this["tickers"].asJsonArray.map { ticker -> ticker.asString }
        )

@Service
class SubscribeMarketMessageResolver(private val gson: Gson,
                                     private val webSocketService: WebSocketService,
                                     private val sessionMappingService: SessionMappingService) : WsMessageResolver {
    override fun getMessage(): String = "subscribe_market"


    override fun process(messageJson: JsonObject, session: WebSocketSession): Unit =
            messageJson.mapToMarkerSubscriptionDetails().let { handleMarketSubscriptionRequest(session, it) }


    private fun handleMarketSubscriptionRequest(session: WebSocketSession, markerSubscriptionDetails: com.walletsphere.pricingservice.model.MarkerSubscriptionDetails) =
        sessionMappingService.getUserId(session).takeIf { it != 0L }
                ?.let {
                    webSocketService.subscribe(it, markerSubscriptionDetails)
                    sendLatestPrices(it, markerSubscriptionDetails.exchanger, markerSubscriptionDetails.allTickers)
                }

    private fun sendLatestPrices(accountId: Long, exchanger: CryptoExchanger, tickers: List<String>) =
            gson.toJson(webSocketService.getLastPrices(exchanger, tickers))
                    ?.let { sessionMappingService.sendMessageToSession(accountId, it) }
}
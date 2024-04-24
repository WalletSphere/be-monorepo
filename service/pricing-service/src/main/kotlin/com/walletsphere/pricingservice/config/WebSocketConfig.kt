package com.walletsphere.pricingservice.config

import com.walletsphere.pricingservice.service.ws.CryptoPriceWebsocketHandler
import com.walletsphere.pricingservice.service.ws.message.WsMessageResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@EnableWebSocket
@Configuration
class WebSocketConfig(private val messageResolvers: List<WsMessageResolver>) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(CryptoPriceWebsocketHandler(messageResolvers), "/crypto-pricing")
    }
}
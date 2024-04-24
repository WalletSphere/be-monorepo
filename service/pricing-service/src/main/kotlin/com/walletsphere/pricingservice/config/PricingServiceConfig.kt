package com.walletsphere.pricingservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class PricingServiceConfig {

    @Bean
    fun getRestTemplate() = RestTemplate();
}
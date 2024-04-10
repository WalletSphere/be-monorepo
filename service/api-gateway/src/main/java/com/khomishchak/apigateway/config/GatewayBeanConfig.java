package com.khomishchak.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GatewayBeanConfig {

    @Bean("wsGatewayRestTemplate")
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}

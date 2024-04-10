package com.khomishchak.authservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AuthServiceConfig {

    @Bean("wsAuthServiceRestTemplate")
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean("wsAuthServiceObjectMapper")
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}

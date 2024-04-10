package com.khomishchak.ws.services.integration.whitebit.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WhiteBitWebConfig {

    @Bean("WhiteBitApiHttpClient")
    HttpClient whiteBitApiHttpClient(
            @Value("${ws.integration.whitebit.api.timeout.response:10}") int responseTimeoutSeconds,
            @Value("${ws.integration.whitebit.api.timeout.connection:5}") int connectionTimeoutSeconds,
            @Value("${ws.integration.whitebit.api.timeout.read:5}") int readTimeoutSeconds,
            @Value("${ws.integration.whitebit.api.timeout.write:5}") int writeTimeoutSeconds) {

        return HttpClient.create()
                .responseTimeout(Duration.ofSeconds(responseTimeoutSeconds))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000 * connectionTimeoutSeconds)
                .doOnConnected(conn -> conn
                        .addHandlerFirst(new ReadTimeoutHandler(readTimeoutSeconds))
                        .addHandlerFirst(new WriteTimeoutHandler(writeTimeoutSeconds)));
    }

    @Bean("WhiteBitApiWebClient")
    WebClient WhiteBitApiShortTimeoutWebClient(WebClient.Builder webClientBuilder,
            @Qualifier("WhiteBitApiHttpClient") HttpClient idScanApiHttpClient) {
        return webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(idScanApiHttpClient))
                .build();
    }

    @Bean("WhiteBitObjectMapper")
    ObjectMapper WhiteBitObjectMapper() {
        return new ObjectMapper();
    }
}

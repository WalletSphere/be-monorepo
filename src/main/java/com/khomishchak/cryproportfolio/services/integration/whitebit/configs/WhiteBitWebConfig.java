package com.khomishchak.cryproportfolio.services.integration.whitebit.configs;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WhiteBitWebConfig {

    @Qualifier("WhiteBitApiHttpClient")
    @Bean
    HttpClient whiteBitApiHttpClient(
            @Value("${crypto-portfolio.integration.whitebit.api.timeout.response:10}") int responseTimeoutSeconds,
            @Value("${crypto-portfolio.integration.whitebit.api.timeout.connection:5}") int connectionTimeoutSeconds,
            @Value("${crypto-portfolio.integration.whitebit.api.timeout.read:5}") int readTimeoutSeconds,
            @Value("${crypto-portfolio.integration.whitebit.api.timeout.write:5}") int writeTimeoutSeconds) {

        return HttpClient.create()
                .responseTimeout(Duration.ofSeconds(responseTimeoutSeconds))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000 * connectionTimeoutSeconds)
                .doOnConnected(conn -> conn
                        .addHandlerFirst(new ReadTimeoutHandler(readTimeoutSeconds))
                        .addHandlerFirst(new WriteTimeoutHandler(writeTimeoutSeconds)));
    }

    @Qualifier("WhiteBitApiWebClient")
    @Bean
    WebClient idScanApiShortTimeoutWebClient(WebClient.Builder webClientBuilder,
            @Qualifier("WhiteBitApiHttpClient") HttpClient idScanApiHttpClient) {
        return webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(idScanApiHttpClient))
                .build();
    }
}

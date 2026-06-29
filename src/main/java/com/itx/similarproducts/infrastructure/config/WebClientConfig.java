package com.itx.similarproducts.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient externalApiWebClient(@Value("${external.api.base-url}") String baseUrl) {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("external-api")
                .maxConnections(500)
                .pendingAcquireMaxCount(1000)
                .pendingAcquireTimeout(Duration.ofSeconds(10))
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider);

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}

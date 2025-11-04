package com.portal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class YandexDiskConfig {

    @Value("${spring.yandex}")
    private String accessToken;

    @Bean
    public WebClient yandexDiskWebClient() {
        return WebClient.builder()
                .baseUrl("https://cloud-api.yandex.net/v1/disk")
                .defaultHeader("Authorization", "OAuth " + accessToken)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
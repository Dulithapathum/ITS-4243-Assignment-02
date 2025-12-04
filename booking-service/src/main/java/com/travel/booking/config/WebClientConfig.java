package com.travel.booking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    
    @Value("${services.user.url}")
    private String userServiceUrl;
    
    @Value("${services.notification.url}")
    private String notificationServiceUrl;
    
    @Bean
    public WebClient userWebClient() {
        return WebClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }
    
    @Bean
    public WebClient notificationWebClient() {
        return WebClient.builder()
                .baseUrl(notificationServiceUrl)
                .build();
    }
}


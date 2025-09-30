package com.fitness.activityservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    @LoadBalanced //allows WebClient to use service names for load balancing
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
    @Bean  //will create a WebClient instance with the base URL set to the user-service
    public WebClient userServiceWebClient(WebClient.Builder webClientBuilder) {
        return  webClientBuilder
                .baseUrl("http://USER-SERVICE")
                .build(); // Base URL for user-service
    }
}

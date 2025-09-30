package com.fitness.activityservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class UserDetailsWebClient {

    private final WebClient userServiceClient;

    public Boolean validateUser(String userId){
        return userServiceClient.get()
                .uri("/api/users/{userId}/validate",userId)
                .retrieve() // retrieve() fetches the response
                .bodyToMono(Boolean.class) // body to mono converts the response body to a Mono
                .block(); // block() is used to get the actual value from the Mono
    }
}

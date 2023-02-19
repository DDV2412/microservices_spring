package com.ipmugo.articleservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder(){
        ExchangeFilterFunction addAccessTokenHeader = (clientRequest, next) -> {
            final HttpHeaders headers = clientRequest.headers();
            final String accessToken = headers.getFirst("accessToken");

            return next.exchange(ClientRequest.from(clientRequest)
                    .header("Authorization", "Bearer " + accessToken)
                    .build());
        };

        return WebClient.builder().filter(addAccessTokenHeader);
    }

    @Bean
    public WebClient.Builder webClientWithOutBuilder(){
        return WebClient.builder();
    }
}

package com.mediahub.contentcatalog.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class NotificationClient {

    @Autowired
    private WebClient.Builder loadBalancedWebClientBuilder;

    // "notification-service" is the logical name that service will register
    // under with Eureka (its spring.application.name) -- once that service
    // exists and registers itself, this call resolves automatically without
    // a hardcoded host:port.
    public void sendNotification(Map<String, Object> request) {

        loadBalancedWebClientBuilder.build()
                .post()
                .uri("http://notification-service/mediaHub/notifications/createNotification/v1.0")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe();
    }
}
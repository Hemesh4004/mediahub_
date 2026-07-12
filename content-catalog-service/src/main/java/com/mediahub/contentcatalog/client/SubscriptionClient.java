package com.mediahub.contentcatalog.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class SubscriptionClient {

    @Autowired
    private WebClient.Builder loadBalancedWebClientBuilder;

    // "subscription-service" is the logical name that service will register
    // under with Eureka once it exists -- resolved via load balancer, no
    // hardcoded host:port needed.
    public Map validateSubscription(Long userId) {

        return loadBalancedWebClientBuilder.build()
                .get()
                .uri("http://subscription-service/mediaHub/subscriptionPlan/usersubscriptions/validateSubscription/"
                        + userId)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}

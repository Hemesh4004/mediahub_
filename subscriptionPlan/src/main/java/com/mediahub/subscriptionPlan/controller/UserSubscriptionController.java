package com.mediahub.subscriptionPlan.controller;

import com.mediahub.subscriptionPlan.dto.CreateSubscriptionRequest;
import com.mediahub.subscriptionPlan.dto.UpdateSubscriptionRequest;
import com.mediahub.subscriptionPlan.model.UserSubscription;
import com.mediahub.subscriptionPlan.service.UserSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mediaHub/subscriptionPlan/usersubscriptions")

@Tag(
        name = "User Subscriptions",
        description = "APIs for subscription operations"
)

public class UserSubscriptionController {

    @Autowired
    private UserSubscriptionService userSubscriptionService;


    @Operation(summary = "Create Subscription")
    @PostMapping("/createSubscription")
    public ResponseEntity<?> createSubscription(@RequestBody CreateSubscriptionRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(userSubscriptionService.createSubscription(request));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Missing or invalid fields"));
        }
    }

    @Operation(summary = "Get All Subscriptions")
    @GetMapping("/fetchSubscriptions")
    public ResponseEntity<?> fetchSubscriptions() {
        try {
            List<UserSubscription> list = userSubscriptionService.fetchSubscriptions();
            return ResponseEntity.ok(list);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No subscriptions found"));
        }
    }

    @Operation(summary = "Get Subscription By ID")
    @GetMapping("/fetchSubscription/{subscriptionId}")
    public ResponseEntity<?> fetchSubscriptionById(@PathVariable Long subscriptionId) {
        try {
            return ResponseEntity.ok(
                    userSubscriptionService.fetchSubscriptionById(subscriptionId));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Subscription not found"));
        }
    }

    @Operation(summary = "Update Subscription")
    @PutMapping("/updateSubscription/{subscriptionId}")
    public ResponseEntity<?> updateSubscription(
            @PathVariable Long subscriptionId,
            @RequestBody UpdateSubscriptionRequest request) {

        try {
            return ResponseEntity.ok(
                    userSubscriptionService.updateSubscription(subscriptionId, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    @Operation(summary = "Renew Subscription")
    @PutMapping("/renewSubscription/{subscriptionId}")
    public ResponseEntity<?> renewSubscription(
            @PathVariable Long subscriptionId,
            @RequestBody UpdateSubscriptionRequest request) {

        try {
            return ResponseEntity.ok(
                    userSubscriptionService.renewSubscription(subscriptionId, request));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    @Operation(summary = "Cancel Subscription")
    @PutMapping("/cancelSubscription/{subscriptionId}")
    public ResponseEntity<?> cancelSubscription(
            @PathVariable Long subscriptionId) {

        try {
            return ResponseEntity.ok(
                    userSubscriptionService.cancelSubscription(subscriptionId));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", ex.getMessage()));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    // ✅ ANALYTICS API
    @GetMapping("/analytics")
    public ResponseEntity<?> getSubscriptionAnalytics() {
        try {
            return ResponseEntity.ok(
                    userSubscriptionService.getSubscriptionAnalytics());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Unable to fetch analytics"));
        }
    }

    @GetMapping("/validateSubscription/{userId}")
    public ResponseEntity<?> validateSubscription(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                userSubscriptionService
                        .validateSubscription(userId));
    }
}
package com.firstclub.membership.membership.web;

import com.firstclub.membership.membership.service.SubscriptionService;
import com.firstclub.membership.membership.web.dto.MembershipView;
import com.firstclub.membership.membership.web.dto.SubscribeRequest;
import com.firstclub.membership.membership.web.dto.TierChangeRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Intent-revealing commands (not generic CRUD) for the subscription lifecycle. */
@RestController
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/api/users/{userId}/subscription")
    @ResponseStatus(HttpStatus.CREATED)
    public MembershipView subscribe(@PathVariable Long userId, @Valid @RequestBody SubscribeRequest request) {
        return subscriptionService.subscribe(userId, request);
    }

    @PostMapping("/api/subscriptions/{id}/upgrade")
    public MembershipView upgrade(@PathVariable Long id, @Valid @RequestBody TierChangeRequest request) {
        return subscriptionService.upgrade(id, request.targetTierId());
    }

    @PostMapping("/api/subscriptions/{id}/downgrade")
    public MembershipView downgrade(@PathVariable Long id, @Valid @RequestBody TierChangeRequest request) {
        return subscriptionService.downgrade(id, request.targetTierId());
    }

    @PostMapping("/api/subscriptions/{id}/cancel")
    public MembershipView cancel(@PathVariable Long id) {
        return subscriptionService.cancel(id);
    }

    @GetMapping("/api/users/{userId}/membership")
    public MembershipView membership(@PathVariable Long userId) {
        return subscriptionService.getMembership(userId);
    }
}

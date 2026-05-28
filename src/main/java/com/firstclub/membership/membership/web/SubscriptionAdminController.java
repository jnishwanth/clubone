package com.firstclub.membership.membership.web;

import com.firstclub.membership.membership.service.SubscriptionService;
import com.firstclub.membership.membership.web.dto.ExpirySweepResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/** Manual trigger for the daily expiry sweep, so it can be demoed without waiting. */
@RestController
@RequiredArgsConstructor
public class SubscriptionAdminController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/api/admin/subscriptions/expire-due")
    public ExpirySweepResponse expireDue() {
        return new ExpirySweepResponse(subscriptionService.expireDueSubscriptions());
    }
}

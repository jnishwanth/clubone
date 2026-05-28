package com.firstclub.membership.activity.service;

import com.firstclub.membership.activity.domain.OrderFulfilledEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Inbound adapter for order events (Observer): keeps order ingestion decoupled
 * from tier accounting. The publisher just fires {@link OrderFulfilledEvent};
 * this handler is the only place that knows how to update the activity model.
 */
@Component
@RequiredArgsConstructor
public class OrderEventHandler {

    private final ActivityService activityService;

    @EventListener
    public void onOrderFulfilled(OrderFulfilledEvent event) {
        activityService.applyOrderFulfilled(event);
    }
}

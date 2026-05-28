package com.firstclub.membership.activity.service;

import com.firstclub.membership.activity.domain.OrderFulfilledEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Catches the order-fulfilled event and pushes it into the activity model. Keeps the
 *  publisher decoupled from tier accounting — only this class knows how the read model
 *  updates. */
@Component
@RequiredArgsConstructor
public class OrderEventHandler {

    private final ActivityService activityService;

    @EventListener
    public void onOrderFulfilled(OrderFulfilledEvent event) {
        activityService.applyOrderFulfilled(event);
    }
}

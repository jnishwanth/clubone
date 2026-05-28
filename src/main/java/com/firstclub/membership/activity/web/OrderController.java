package com.firstclub.membership.activity.web;

import com.firstclub.membership.activity.domain.OrderFulfilledEvent;
import com.firstclub.membership.activity.web.dto.OrderEventRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

/**
 * Inbound order-event endpoint. In a real system this is where an external Order
 * service would push fulfilled-order notifications; here it's the demo seam.
 */
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final ApplicationEventPublisher publisher;

    @PostMapping("/api/orders/events")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void recordOrder(@Valid @RequestBody OrderEventRequest request) {
        String period = (request.period() == null || request.period().isBlank())
                ? YearMonth.now().toString()
                : request.period();
        publisher.publishEvent(new OrderFulfilledEvent(request.userId(), period, request.amount()));
    }
}

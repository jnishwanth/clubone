package com.firstclub.membership.activity.domain;

import java.math.BigDecimal;

/**
 * Domain event: an order was fulfilled for a user in a period. Published by the
 * inbound order adapter; consumed by the activity slice to update the read model.
 * Decouples order ingestion from tier accounting (Observer).
 */
public record OrderFulfilledEvent(Long userId, String period, BigDecimal amount) {
}

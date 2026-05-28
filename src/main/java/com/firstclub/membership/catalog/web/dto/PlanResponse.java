package com.firstclub.membership.catalog.web.dto;

import com.firstclub.membership.catalog.domain.BillingPeriod;

import java.math.BigDecimal;

public record PlanResponse(
        Long id,
        String name,
        BillingPeriod billingPeriod,
        BigDecimal price,
        boolean active
) {
}

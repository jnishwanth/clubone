package com.firstclub.membership.catalog.web.dto;

import com.firstclub.membership.catalog.domain.BillingPeriod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record PlanRequest(
        @NotBlank String name,
        @NotNull BillingPeriod billingPeriod,
        @NotNull @PositiveOrZero BigDecimal price,
        Boolean active
) {
}

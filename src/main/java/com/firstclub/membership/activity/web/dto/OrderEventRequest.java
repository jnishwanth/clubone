package com.firstclub.membership.activity.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderEventRequest(
        @NotNull Long userId,
        @NotNull @Positive BigDecimal amount,
        /** Optional "YYYY-MM"; defaults to the current month. */
        String period
) {
}

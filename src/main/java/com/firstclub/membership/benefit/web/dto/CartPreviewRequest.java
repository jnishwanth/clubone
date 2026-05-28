package com.firstclub.membership.benefit.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CartPreviewRequest(
        @NotNull @PositiveOrZero BigDecimal cartTotal,
        @PositiveOrZero BigDecimal deliveryFee
) {
}

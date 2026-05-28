package com.firstclub.membership.catalog.web.dto;

import com.firstclub.membership.catalog.domain.BenefitType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BenefitDto(
        @NotNull BenefitType type,
        BigDecimal value,
        String description
) {
}

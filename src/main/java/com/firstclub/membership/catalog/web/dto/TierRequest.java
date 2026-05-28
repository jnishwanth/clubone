package com.firstclub.membership.catalog.web.dto;

import com.firstclub.membership.catalog.domain.CriteriaCombinator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;

public record TierRequest(
        @NotBlank String name,
        @NotNull @PositiveOrZero Integer rank,
        @NotNull @PositiveOrZero BigDecimal joiningFee,
        @NotNull @PositiveOrZero BigDecimal monthlyFee,
        Boolean active,
        @NotNull CriteriaCombinator criteriaCombinator,
        @Valid List<BenefitDto> benefits,
        @Valid List<CriterionDto> criteria
) {
}

package com.firstclub.membership.catalog.web.dto;

import com.firstclub.membership.catalog.domain.ComparisonOperator;
import com.firstclub.membership.catalog.domain.CriterionType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CriterionDto(
        @NotNull CriterionType type,
        ComparisonOperator operator,
        BigDecimal threshold,
        String stringValue
) {
}

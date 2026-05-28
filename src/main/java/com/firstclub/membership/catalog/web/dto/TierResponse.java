package com.firstclub.membership.catalog.web.dto;

import com.firstclub.membership.catalog.domain.CriteriaCombinator;

import java.math.BigDecimal;
import java.util.List;

public record TierResponse(
        Long id,
        String name,
        int rank,
        BigDecimal joiningFee,
        BigDecimal monthlyFee,
        boolean active,
        CriteriaCombinator criteriaCombinator,
        List<BenefitDto> benefits,
        List<CriterionDto> criteria
) {
}

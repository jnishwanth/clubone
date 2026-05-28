package com.firstclub.membership.catalog.web.dto;

import com.firstclub.membership.catalog.domain.Plan;
import com.firstclub.membership.catalog.domain.Tier;

import java.util.List;

/** Entity → response mapping for the catalog slice. Keeps entities off the wire. */
public final class CatalogMapper {

    private CatalogMapper() {
    }

    public static PlanResponse toResponse(Plan plan) {
        return new PlanResponse(
                plan.getId(),
                plan.getName(),
                plan.getBillingPeriod(),
                plan.getPrice().amount(),
                plan.isActive());
    }

    public static TierResponse toResponse(Tier tier) {
        List<BenefitDto> benefits = tier.getBenefits().stream()
                .map(b -> new BenefitDto(b.getType(), b.getValue(), b.getDescription()))
                .toList();
        List<CriterionDto> criteria = tier.getQualifyingCriteria().stream()
                .map(c -> new CriterionDto(c.getType(), c.getOperator(), c.getThreshold(), c.getStringValue()))
                .toList();
        return new TierResponse(
                tier.getId(),
                tier.getName(),
                tier.getRank(),
                tier.getJoiningFee().amount(),
                tier.getMonthlyFee().amount(),
                tier.isActive(),
                tier.getCriteriaCombinator(),
                benefits,
                criteria);
    }
}

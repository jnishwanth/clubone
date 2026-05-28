package com.firstclub.membership.benefit.application.impl;

import com.firstclub.membership.benefit.application.BenefitApplier;
import com.firstclub.membership.benefit.application.CartCalculation;
import com.firstclub.membership.catalog.domain.BenefitType;
import com.firstclub.membership.catalog.domain.TierBenefit;
import org.springframework.stereotype.Component;

/**
 * Entitlement, not cart math — surfaces in {@code GET /api/users/{id}/benefits} via the
 * tier's benefit list. Deliberately a no-op here so the registry has an entry for every
 * benefit type and the "always one bean per type" invariant holds.
 */
@Component
public class ExclusiveDealsApplier implements BenefitApplier {

    @Override
    public BenefitType type() {
        return BenefitType.EXCLUSIVE_DEALS;
    }

    @Override
    public void apply(TierBenefit benefit, CartCalculation calc) {
        // No cart effect.
    }
}

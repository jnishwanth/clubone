package com.firstclub.membership.benefit.application.impl;

import com.firstclub.membership.benefit.application.BenefitApplier;
import com.firstclub.membership.benefit.application.CartCalculation;
import com.firstclub.membership.catalog.domain.BenefitType;
import com.firstclub.membership.catalog.domain.TierBenefit;
import org.springframework.stereotype.Component;

/**
 * Entitlement, not cart math — see {@link ExclusiveDealsApplier} for the rationale.
 */
@Component
public class PrioritySupportApplier implements BenefitApplier {

    @Override
    public BenefitType type() {
        return BenefitType.PRIORITY_SUPPORT;
    }

    @Override
    public void apply(TierBenefit benefit, CartCalculation calc) {
        // No cart effect.
    }
}

package com.firstclub.membership.benefit.application.impl;

import com.firstclub.membership.benefit.application.BenefitApplier;
import com.firstclub.membership.benefit.application.CartCalculation;
import com.firstclub.membership.catalog.domain.BenefitType;
import com.firstclub.membership.catalog.domain.TierBenefit;
import org.springframework.stereotype.Component;

@Component
public class FreeDeliveryApplier implements BenefitApplier {

    @Override
    public BenefitType type() {
        return BenefitType.FREE_DELIVERY;
    }

    @Override
    public void apply(TierBenefit benefit, CartCalculation calc) {
        calc.waiveDelivery();
        calc.recordApplied(type());
    }
}

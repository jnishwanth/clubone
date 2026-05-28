package com.firstclub.membership.benefit.application.impl;

import com.firstclub.membership.benefit.application.BenefitApplier;
import com.firstclub.membership.benefit.application.CartCalculation;
import com.firstclub.membership.catalog.domain.BenefitType;
import com.firstclub.membership.catalog.domain.TierBenefit;
import org.springframework.stereotype.Component;

@Component
public class ExtraDiscountPercentApplier implements BenefitApplier {

    @Override
    public BenefitType type() {
        return BenefitType.EXTRA_DISCOUNT_PERCENT;
    }

    @Override
    public void apply(TierBenefit benefit, CartCalculation calc) {
        if (benefit.getValue() == null || benefit.getValue().signum() <= 0) {
            return;
        }
        calc.setDiscountPercent(benefit.getValue());
        calc.setDiscount(calc.getCartTotal().percentOf(benefit.getValue()));
        calc.recordApplied(type());
    }
}

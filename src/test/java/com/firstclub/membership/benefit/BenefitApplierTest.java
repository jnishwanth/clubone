package com.firstclub.membership.benefit;

import com.firstclub.membership.benefit.application.CartCalculation;
import com.firstclub.membership.benefit.application.impl.ExtraDiscountPercentApplier;
import com.firstclub.membership.benefit.application.impl.FreeDeliveryApplier;
import com.firstclub.membership.catalog.domain.BenefitType;
import com.firstclub.membership.catalog.domain.TierBenefit;
import com.firstclub.membership.common.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BenefitApplierTest {

    @Test
    void extraDiscountPercentReducesCartByThePercentage() {
        ExtraDiscountPercentApplier applier = new ExtraDiscountPercentApplier();
        CartCalculation calc = new CartCalculation(BigDecimal.valueOf(2000), BigDecimal.valueOf(50));
        TierBenefit benefit = benefit(BenefitType.EXTRA_DISCOUNT_PERCENT, BigDecimal.valueOf(5));

        applier.apply(benefit, calc);

        // 5% of 2000 = 100; delivery untouched at 50; payable = 2000 - 100 + 50 = 1950.
        assertThat(calc.getDiscount()).isEqualTo(Money.ofRupees(100));
        assertThat(calc.getDiscountPercent()).isEqualByComparingTo("5");
        assertThat(calc.payable()).isEqualTo(Money.ofRupees(1950));
        assertThat(calc.getAppliedBenefits()).containsExactly(BenefitType.EXTRA_DISCOUNT_PERCENT);
    }

    @Test
    void extraDiscountPercentIsNoOpWhenValueIsMissingOrZero() {
        ExtraDiscountPercentApplier applier = new ExtraDiscountPercentApplier();
        CartCalculation calc = new CartCalculation(BigDecimal.valueOf(2000), BigDecimal.valueOf(50));

        applier.apply(benefit(BenefitType.EXTRA_DISCOUNT_PERCENT, null), calc);
        applier.apply(benefit(BenefitType.EXTRA_DISCOUNT_PERCENT, BigDecimal.ZERO), calc);

        assertThat(calc.getDiscount()).isEqualTo(Money.ZERO);
        assertThat(calc.getAppliedBenefits()).isEmpty();
    }

    @Test
    void freeDeliveryWaivesTheDeliveryFee() {
        FreeDeliveryApplier applier = new FreeDeliveryApplier();
        CartCalculation calc = new CartCalculation(BigDecimal.valueOf(2000), BigDecimal.valueOf(50));

        applier.apply(benefit(BenefitType.FREE_DELIVERY, null), calc);

        // Delivery removed; payable = 2000 - 0 + 0 = 2000.
        assertThat(calc.isDeliveryWaived()).isTrue();
        assertThat(calc.payable()).isEqualTo(Money.ofRupees(2000));
        assertThat(calc.getAppliedBenefits()).containsExactly(BenefitType.FREE_DELIVERY);
    }

    @Test
    void appliersComposeAsTheServiceWouldRunThem() {
        ExtraDiscountPercentApplier discount = new ExtraDiscountPercentApplier();
        FreeDeliveryApplier delivery = new FreeDeliveryApplier();
        CartCalculation calc = new CartCalculation(BigDecimal.valueOf(2000), BigDecimal.valueOf(50));

        discount.apply(benefit(BenefitType.EXTRA_DISCOUNT_PERCENT, BigDecimal.valueOf(5)), calc);
        delivery.apply(benefit(BenefitType.FREE_DELIVERY, null), calc);

        // 5% off + delivery waived → matches the Gold-tier preview path: payable 1900.
        assertThat(calc.payable()).isEqualTo(Money.ofRupees(1900));
        assertThat(calc.getAppliedBenefits())
                .containsExactly(BenefitType.EXTRA_DISCOUNT_PERCENT, BenefitType.FREE_DELIVERY);
    }

    private TierBenefit benefit(BenefitType type, BigDecimal value) {
        TierBenefit b = new TierBenefit();
        b.setType(type);
        b.setValue(value);
        return b;
    }
}

package com.firstclub.membership.benefit.application;

import com.firstclub.membership.catalog.domain.BenefitType;
import com.firstclub.membership.common.Money;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Running totals for one preview. Appliers mutate, service reads back. One per request,
 *  not thread-safe. */
@Getter
public class CartCalculation {

    private final Money cartTotal;
    private final Money deliveryFee;

    private Money discount = Money.ZERO;
    private BigDecimal discountPercent = BigDecimal.ZERO;
    private boolean deliveryWaived = false;
    private final List<BenefitType> appliedBenefits = new ArrayList<>();

    public CartCalculation(BigDecimal cartTotal, BigDecimal deliveryFee) {
        this.cartTotal = Money.of(cartTotal);
        this.deliveryFee = Money.of(deliveryFee == null ? BigDecimal.ZERO : deliveryFee);
    }

    public void setDiscount(Money discount) {
        this.discount = discount;
    }

    public void setDiscountPercent(BigDecimal percent) {
        this.discountPercent = percent;
    }

    public void waiveDelivery() {
        this.deliveryWaived = true;
    }

    public void recordApplied(BenefitType type) {
        this.appliedBenefits.add(type);
    }

    public Money payable() {
        Money effectiveDelivery = deliveryWaived ? Money.ZERO : deliveryFee;
        return cartTotal.minus(discount).plus(effectiveDelivery);
    }
}

package com.firstclub.membership.benefit.web.dto;

import com.firstclub.membership.catalog.domain.BenefitType;

import java.math.BigDecimal;
import java.util.List;

/** Shows how the held tier's benefits apply to a cart (checkout-journey integration). */
public record BenefitPreviewResponse(
        String tierName,
        BigDecimal cartTotal,
        BigDecimal discountPercent,
        BigDecimal discountAmount,
        BigDecimal deliveryFee,
        boolean deliveryWaived,
        BigDecimal payableTotal,
        List<BenefitType> appliedBenefits
) {
}

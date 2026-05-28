package com.firstclub.membership.benefit.service;

import com.firstclub.membership.benefit.web.dto.BenefitPreviewResponse;
import com.firstclub.membership.benefit.web.dto.CartPreviewRequest;
import com.firstclub.membership.catalog.domain.BenefitType;
import com.firstclub.membership.catalog.domain.Tier;
import com.firstclub.membership.catalog.domain.TierBenefit;
import com.firstclub.membership.catalog.service.TierService;
import com.firstclub.membership.catalog.web.dto.BenefitDto;
import com.firstclub.membership.catalog.web.dto.CatalogMapper;
import com.firstclub.membership.common.Money;
import com.firstclub.membership.membership.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Resolves a user's active entitlements (their held tier's benefits) and applies
 * them to a cart — the "integrated with checkout" piece, without building checkout.
 */
@Service
@RequiredArgsConstructor
public class EntitlementService {

    private final SubscriptionService subscriptionService;
    private final TierService tierService;

    @Transactional(readOnly = true)
    public List<BenefitDto> activeBenefits(Long userId) {
        Tier held = tierService.requireById(subscriptionService.activeHeldTierId(userId));
        return CatalogMapper.toResponse(held).benefits();
    }

    @Transactional(readOnly = true)
    public BenefitPreviewResponse preview(Long userId, CartPreviewRequest request) {
        Tier held = tierService.requireById(subscriptionService.activeHeldTierId(userId));

        BigDecimal discountPercent = BigDecimal.ZERO;
        boolean freeDelivery = false;
        List<BenefitType> applied = new ArrayList<>();

        for (TierBenefit benefit : held.getBenefits()) {
            switch (benefit.getType()) {
                case EXTRA_DISCOUNT_PERCENT -> {
                    if (benefit.getValue() != null && benefit.getValue().signum() > 0) {
                        discountPercent = benefit.getValue();
                        applied.add(BenefitType.EXTRA_DISCOUNT_PERCENT);
                    }
                }
                case FREE_DELIVERY -> {
                    freeDelivery = true;
                    applied.add(BenefitType.FREE_DELIVERY);
                }
                default -> {
                    // EXCLUSIVE_DEALS / PRIORITY_SUPPORT don't affect cart math
                }
            }
        }

        Money cartTotal = Money.of(request.cartTotal());
        Money deliveryFee = Money.of(request.deliveryFee() == null ? BigDecimal.ZERO : request.deliveryFee());
        Money discount = discountPercent.signum() > 0 ? cartTotal.percentOf(discountPercent) : Money.ZERO;
        Money effectiveDelivery = freeDelivery ? Money.ZERO : deliveryFee;
        Money payable = cartTotal.minus(discount).plus(effectiveDelivery);

        return new BenefitPreviewResponse(
                held.getName(),
                cartTotal.amount(),
                discountPercent,
                discount.amount(),
                deliveryFee.amount(),
                freeDelivery,
                payable.amount(),
                applied);
    }
}

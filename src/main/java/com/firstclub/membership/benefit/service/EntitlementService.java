package com.firstclub.membership.benefit.service;

import com.firstclub.membership.benefit.application.BenefitApplierRegistry;
import com.firstclub.membership.benefit.application.CartCalculation;
import com.firstclub.membership.benefit.web.dto.BenefitPreviewResponse;
import com.firstclub.membership.benefit.web.dto.CartPreviewRequest;
import com.firstclub.membership.catalog.domain.Tier;
import com.firstclub.membership.catalog.domain.TierBenefit;
import com.firstclub.membership.catalog.service.TierService;
import com.firstclub.membership.catalog.web.dto.BenefitDto;
import com.firstclub.membership.catalog.web.dto.CatalogMapper;
import com.firstclub.membership.membership.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** A user's active entitlements and a cart preview. Benefit application is delegated to
 *  per-type {@link com.firstclub.membership.benefit.application.BenefitApplier} beans, so
 *  adding a benefit type doesn't touch this class. */
@Service
@RequiredArgsConstructor
public class EntitlementService {

    private final SubscriptionService subscriptionService;
    private final TierService tierService;
    private final BenefitApplierRegistry applierRegistry;

    @Transactional(readOnly = true)
    public List<BenefitDto> activeBenefits(Long userId) {
        Tier held = tierService.requireById(subscriptionService.activeHeldTierId(userId));
        return CatalogMapper.toResponse(held).benefits();
    }

    @Transactional(readOnly = true)
    public BenefitPreviewResponse preview(Long userId, CartPreviewRequest request) {
        Tier held = tierService.requireById(subscriptionService.activeHeldTierId(userId));

        CartCalculation calc = new CartCalculation(request.cartTotal(), request.deliveryFee());
        for (TierBenefit benefit : held.getBenefits()) {
            applierRegistry.get(benefit.getType()).apply(benefit, calc);
        }

        return new BenefitPreviewResponse(
                held.getName(),
                calc.getCartTotal().amount(),
                calc.getDiscountPercent(),
                calc.getDiscount().amount(),
                calc.getDeliveryFee().amount(),
                calc.isDeliveryWaived(),
                calc.payable().amount(),
                calc.getAppliedBenefits());
    }
}

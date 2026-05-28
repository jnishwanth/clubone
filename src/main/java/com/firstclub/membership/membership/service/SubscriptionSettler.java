package com.firstclub.membership.membership.service;

import com.firstclub.membership.activity.domain.ActivitySnapshot;
import com.firstclub.membership.activity.service.ActivityService;
import com.firstclub.membership.catalog.domain.Tier;
import com.firstclub.membership.catalog.service.PolicyService;
import com.firstclub.membership.catalog.service.TierService;
import com.firstclub.membership.membership.domain.Subscription;
import com.firstclub.membership.membership.repository.SubscriptionRepository;
import com.firstclub.membership.payment.domain.Payment;
import com.firstclub.membership.payment.domain.PaymentPurpose;
import com.firstclub.membership.payment.service.PaymentService;
import com.firstclub.membership.tiering.criteria.EvaluationContext;
import com.firstclub.membership.tiering.eligibility.TierEligibilityEvaluator;
import com.firstclub.membership.tiering.settlement.SettlementDecision;
import com.firstclub.membership.tiering.settlement.TierSettlementPolicy;
import com.firstclub.membership.tiering.settlement.TierSettlementService;
import com.firstclub.membership.user.domain.User;
import com.firstclub.membership.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

/** Settles ONE subscription for one period in its own tx, so a single failure (or @Version
 *  retry) doesn't roll back the whole batch. Idempotent via the unique (subscription, period)
 *  constraint on TierSettlement. */
@Component
@RequiredArgsConstructor
public class SubscriptionSettler {

    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;
    private final ActivityService activityService;
    private final TierService tierService;
    private final PolicyService policyService;
    private final TierEligibilityEvaluator eligibilityEvaluator;
    private final TierSettlementPolicy settlementPolicy;
    private final TierSettlementService settlementService;
    private final PaymentService paymentService;

    @Transactional
    public void settle(Long subscriptionId, String period) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId).orElse(null);
        if (subscription == null || !subscription.isActive()) {
            return;
        }
        if (settlementService.alreadySettled(subscriptionId, period)) {
            return; // idempotent: already settled this period
        }

        User user = userService.requireById(subscription.getUserId());
        ActivitySnapshot snapshot = activityService.snapshot(subscription.getUserId(), YearMonth.parse(period));
        EvaluationContext context = new EvaluationContext(user.getCohort(), snapshot);

        List<Tier> activeTiers = tierService.activeTiersByRank();
        Tier held = tierService.requireById(subscription.getHeldTierId());
        Tier freeEligible = eligibilityEvaluator.freeEligibleTier(activeTiers, context);

        SettlementDecision decision = settlementPolicy.settle(held, freeEligible);

        if (!decision.feeRequired()) {
            // Earned the held tier or higher for free → settle there (auto-promote/keep).
            subscription.setHeldTierId(decision.tierIfUnpaid().getId());
            subscriptionRepository.save(subscription);
            settlementService.recordWaived(subscriptionId, period, held.getId(), freeEligible.getId());
        } else {
            // Owe the difference: invoice it and open the grace window; tier held meanwhile.
            Payment invoice = paymentService.createInvoice(
                    subscription.getUserId(), subscriptionId, decision.feeOwed(), PaymentPurpose.TIER_MAINTENANCE);
            Instant graceDeadline = Instant.now().plus(policyService.graceWindowDays(), ChronoUnit.DAYS);
            settlementService.recordInvoiced(subscriptionId, period, held.getId(), freeEligible.getId(),
                    decision.feeOwed(), graceDeadline, invoice.getId());
        }
    }

    @Transactional
    public void downgradeForExpiredGrace(Long settlementId, Long subscriptionId, Long freeEligibleTierId) {
        // Compare-and-set guards against racing a late payment; only the winner downgrades.
        if (settlementService.markDowngraded(settlementId)) {
            Subscription subscription = subscriptionRepository.findById(subscriptionId).orElse(null);
            if (subscription != null && subscription.isActive()) {
                subscription.setHeldTierId(freeEligibleTierId);
                subscriptionRepository.save(subscription);
            }
        }
    }
}

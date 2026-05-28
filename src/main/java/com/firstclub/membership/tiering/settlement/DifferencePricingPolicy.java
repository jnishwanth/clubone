package com.firstclub.membership.tiering.settlement;

import com.firstclub.membership.catalog.domain.Tier;
import com.firstclub.membership.common.Money;
import org.springframework.stereotype.Component;

/** Credit-card-style fee waiver. Earn the held tier (or better) for free => no charge,
 *  auto-promote if they earned higher. Otherwise they owe only the gap:
 *  {@code feeOwed = heldTier.monthlyFee - freeEligibleTier.monthlyFee}. */
@Component
public class DifferencePricingPolicy implements TierSettlementPolicy {

    @Override
    public SettlementDecision settle(Tier heldTier, Tier freeEligibleTier) {
        if (freeEligibleTier.getRank() >= heldTier.getRank()) {
            // Earned the held tier or better for free → no charge, settle to the free tier.
            return new SettlementDecision(Money.ZERO, freeEligibleTier, freeEligibleTier);
        }
        // atLeastZero: belt-and-braces — the rank check above already guarantees the difference is positive.
        Money feeOwed = heldTier.getMonthlyFee().minus(freeEligibleTier.getMonthlyFee()).atLeastZero();
        return new SettlementDecision(feeOwed, heldTier, freeEligibleTier);
    }
}

package com.firstclub.membership.tiering.settlement;

import com.firstclub.membership.catalog.domain.Tier;
import com.firstclub.membership.common.Money;
import org.springframework.stereotype.Component;

/**
 * Credit-card-style difference pricing. If the user earns their held tier (or
 * higher) for free, no charge — and they auto-promote to the higher free tier.
 * Otherwise they owe only the gap to the highest tier they earned free:
 * {@code feeOwed = heldTier.monthlyFee - freeEligibleTier.monthlyFee}.
 */
@Component
public class DifferencePricingPolicy implements TierSettlementPolicy {

    @Override
    public SettlementDecision settle(Tier heldTier, Tier freeEligibleTier) {
        if (freeEligibleTier.getRank() >= heldTier.getRank()) {
            // Earned the held tier or better for free → no charge, settle to the free tier.
            return new SettlementDecision(Money.ZERO, freeEligibleTier, freeEligibleTier);
        }
        Money feeOwed = heldTier.getMonthlyFee().minus(freeEligibleTier.getMonthlyFee()).atLeastZero();
        return new SettlementDecision(feeOwed, heldTier, freeEligibleTier);
    }
}

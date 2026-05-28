package com.firstclub.membership.tiering.settlement;

import com.firstclub.membership.catalog.domain.Tier;

/**
 * Strategy for how the monthly tier fee is computed. One implementation ships
 * ({@link DifferencePricingPolicy}); swapping the rule (e.g. full-fee, promo)
 * means a new bean, with no change to the settlement orchestration.
 */
public interface TierSettlementPolicy {

    SettlementDecision settle(Tier heldTier, Tier freeEligibleTier);
}

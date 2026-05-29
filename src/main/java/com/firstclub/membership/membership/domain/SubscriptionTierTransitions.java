package com.firstclub.membership.membership.domain;

import com.firstclub.membership.catalog.domain.Tier;
import com.firstclub.membership.common.BusinessRuleException;

/** Static guards for tier-change validity. Enum + checks; no class-per-state, the rules
 *  are simple rank comparisons. */
public final class SubscriptionTierTransitions {

    private SubscriptionTierTransitions() {
    }

    public static void validateUpgrade(Tier current, Tier target) {
        if (target.getRank() <= current.getRank()) {
            throw new BusinessRuleException(
                    "Upgrade target tier must rank above the current tier (" + current.getName() + ").");
        }
    }

    public static void validateDowngrade(Tier current, Tier target) {
        if (target.getRank() >= current.getRank()) {
            throw new BusinessRuleException(
                    "Downgrade target tier must rank below the current tier (" + current.getName() + ").");
        }
        if (target.getRank() < 0) {
            throw new BusinessRuleException("Cannot downgrade below the base tier.");
        }
    }
}

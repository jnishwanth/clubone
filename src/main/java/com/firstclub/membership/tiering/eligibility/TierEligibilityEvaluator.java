package com.firstclub.membership.tiering.eligibility;

import com.firstclub.membership.catalog.domain.CriteriaCombinator;
import com.firstclub.membership.catalog.domain.Tier;
import com.firstclub.membership.tiering.criteria.AndSpecification;
import com.firstclub.membership.tiering.criteria.CriterionEvaluatorRegistry;
import com.firstclub.membership.tiering.criteria.CriterionSpecification;
import com.firstclub.membership.tiering.criteria.EvaluationContext;
import com.firstclub.membership.tiering.criteria.OrSpecification;
import com.firstclub.membership.tiering.criteria.TierSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Decides which tiers a user qualifies for "free" from their metrics/cohort, by
 * building an And/Or {@link TierSpecification} composite from each tier's criteria.
 */
@Component
@RequiredArgsConstructor
public class TierEligibilityEvaluator {

    private final CriterionEvaluatorRegistry registry;

    public boolean qualifiesFor(Tier tier, EvaluationContext context) {
        // No criteria (e.g. the base tier) = free for everyone.
        if (tier.getQualifyingCriteria().isEmpty()) {
            return true;
        }
        List<TierSpecification> specs = tier.getQualifyingCriteria().stream()
                .map(c -> (TierSpecification) new CriterionSpecification(c, registry.get(c.getType())))
                .toList();
        TierSpecification combined = tier.getCriteriaCombinator() == CriteriaCombinator.ALL
                ? new AndSpecification(specs)
                : new OrSpecification(specs);
        return combined.isSatisfiedBy(context);
    }

    /**
     * Highest-rank active tier the user earns for free. Tiers must be supplied in
     * ascending rank order; the base tier (no criteria) guarantees a non-null result.
     */
    public Tier freeEligibleTier(List<Tier> activeTiersAscending, EvaluationContext context) {
        Tier best = null;
        for (Tier tier : activeTiersAscending) {
            if (qualifiesFor(tier, context) && (best == null || tier.getRank() > best.getRank())) {
                best = tier;
            }
        }
        return best;
    }
}

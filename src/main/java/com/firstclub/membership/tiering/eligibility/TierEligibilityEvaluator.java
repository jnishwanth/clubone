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

/** Builds an And/Or spec tree from a tier's criteria and asks whether the user qualifies
 *  for it for free. */
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

    /** Highest-rank tier the user gets free. Tiers must be in ascending rank order;
     *  base tier has no criteria so always qualifies => result is non-null. */
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

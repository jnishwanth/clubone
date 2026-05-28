package com.firstclub.membership.tiering.criteria;

import com.firstclub.membership.catalog.domain.CriterionConfig;

/** Adapts a configured criterion + its evaluator into a leaf {@link TierSpecification}. */
public record CriterionSpecification(CriterionConfig config, CriterionEvaluator evaluator)
        implements TierSpecification {

    @Override
    public boolean isSatisfiedBy(EvaluationContext context) {
        return evaluator.evaluate(config, context);
    }
}

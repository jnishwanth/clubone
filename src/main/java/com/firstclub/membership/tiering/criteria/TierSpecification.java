package com.firstclub.membership.tiering.criteria;

/**
 * Specification pattern: a yes/no predicate over an {@link EvaluationContext}.
 * Composites (And/Or) and leaf criterion specs all implement this, so the engine
 * combines them uniformly.
 */
public interface TierSpecification {

    boolean isSatisfiedBy(EvaluationContext context);
}

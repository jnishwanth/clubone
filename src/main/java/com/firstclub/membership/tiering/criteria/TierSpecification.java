package com.firstclub.membership.tiering.criteria;

/** A yes/no predicate over an EvaluationContext. Both leaf specs and And/Or composites
 *  implement this so the engine combines them uniformly. */
public interface TierSpecification {

    boolean isSatisfiedBy(EvaluationContext context);
}

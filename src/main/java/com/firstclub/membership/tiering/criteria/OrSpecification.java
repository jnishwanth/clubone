package com.firstclub.membership.tiering.criteria;

import java.util.List;

/** Composite: satisfied when at least one child is (empty = false). */
public record OrSpecification(List<TierSpecification> children) implements TierSpecification {

    @Override
    public boolean isSatisfiedBy(EvaluationContext context) {
        return children.stream().anyMatch(child -> child.isSatisfiedBy(context));
    }
}

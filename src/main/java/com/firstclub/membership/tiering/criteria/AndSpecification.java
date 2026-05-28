package com.firstclub.membership.tiering.criteria;

import java.util.List;

/** Composite: satisfied only when every child is (empty = vacuously true). */
public record AndSpecification(List<TierSpecification> children) implements TierSpecification {

    @Override
    public boolean isSatisfiedBy(EvaluationContext context) {
        return children.stream().allMatch(child -> child.isSatisfiedBy(context));
    }
}

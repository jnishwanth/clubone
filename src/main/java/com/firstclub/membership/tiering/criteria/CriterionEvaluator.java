package com.firstclub.membership.tiering.criteria;

import com.firstclub.membership.catalog.domain.CriterionConfig;
import com.firstclub.membership.catalog.domain.CriterionType;

/**
 * Strategy for one kind of criterion. Each implementation declares the
 * {@link CriterionType} it handles; the registry wires type → evaluator. Adding a
 * new criterion kind is a new bean implementing this interface — no engine edits.
 */
public interface CriterionEvaluator {

    CriterionType type();

    boolean evaluate(CriterionConfig config, EvaluationContext context);
}

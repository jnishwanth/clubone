package com.firstclub.membership.tiering.criteria;

import com.firstclub.membership.catalog.domain.CriterionConfig;
import com.firstclub.membership.catalog.domain.CriterionType;

/** Strategy per criterion type. The registry maps type to evaluator; a new criterion kind
 *  is a new bean implementing this, no engine edits. */
public interface CriterionEvaluator {

    CriterionType type();

    boolean evaluate(CriterionConfig config, EvaluationContext context);
}

package com.firstclub.membership.tiering.criteria.impl;

import com.firstclub.membership.catalog.domain.CriterionConfig;
import com.firstclub.membership.catalog.domain.CriterionType;
import com.firstclub.membership.tiering.criteria.CriterionEvaluator;
import com.firstclub.membership.tiering.criteria.EvaluationContext;
import org.springframework.stereotype.Component;

/** Period-independent: passes when the user belongs to the configured cohort. */
@Component
public class CohortEvaluator implements CriterionEvaluator {

    @Override
    public CriterionType type() {
        return CriterionType.COHORT;
    }

    @Override
    public boolean evaluate(CriterionConfig config, EvaluationContext context) {
        return context.cohort() != null
                && context.cohort().equalsIgnoreCase(config.getStringValue());
    }
}

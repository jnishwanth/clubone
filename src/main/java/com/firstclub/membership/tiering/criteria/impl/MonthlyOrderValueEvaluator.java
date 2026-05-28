package com.firstclub.membership.tiering.criteria.impl;

import com.firstclub.membership.catalog.domain.CriterionConfig;
import com.firstclub.membership.catalog.domain.CriterionType;
import com.firstclub.membership.tiering.criteria.CriterionEvaluator;
import com.firstclub.membership.tiering.criteria.EvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class MonthlyOrderValueEvaluator implements CriterionEvaluator {

    @Override
    public CriterionType type() {
        return CriterionType.MONTHLY_ORDER_VALUE;
    }

    @Override
    public boolean evaluate(CriterionConfig config, EvaluationContext context) {
        return config.getOperator().test(
                context.activity().orderValue().amount(), config.getThreshold());
    }
}

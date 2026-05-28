package com.firstclub.membership.tiering.criteria.impl;

import com.firstclub.membership.catalog.domain.CriterionConfig;
import com.firstclub.membership.catalog.domain.CriterionType;
import com.firstclub.membership.tiering.criteria.CriterionEvaluator;
import com.firstclub.membership.tiering.criteria.EvaluationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ReferralCountEvaluator implements CriterionEvaluator {

    @Override
    public CriterionType type() {
        return CriterionType.REFERRAL_COUNT;
    }

    @Override
    public boolean evaluate(CriterionConfig config, EvaluationContext context) {
        return config.getOperator().test(
                BigDecimal.valueOf(context.activity().referralCount()), config.getThreshold());
    }
}

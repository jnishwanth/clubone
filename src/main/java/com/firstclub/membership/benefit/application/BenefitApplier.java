package com.firstclub.membership.benefit.application;

import com.firstclub.membership.catalog.domain.BenefitType;
import com.firstclub.membership.catalog.domain.TierBenefit;

/** Strategy per benefit type. Same shape as {@code CriterionEvaluator}: add a type =
 *  one bean + one enum constant, no edits to EntitlementService. */
public interface BenefitApplier {

    BenefitType type();

    void apply(TierBenefit benefit, CartCalculation calculation);
}

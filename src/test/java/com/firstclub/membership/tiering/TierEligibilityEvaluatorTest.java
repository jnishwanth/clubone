package com.firstclub.membership.tiering;

import com.firstclub.membership.activity.domain.ActivitySnapshot;
import com.firstclub.membership.catalog.domain.ComparisonOperator;
import com.firstclub.membership.catalog.domain.CriteriaCombinator;
import com.firstclub.membership.catalog.domain.CriterionConfig;
import com.firstclub.membership.catalog.domain.CriterionType;
import com.firstclub.membership.catalog.domain.Tier;
import com.firstclub.membership.common.Money;
import com.firstclub.membership.tiering.criteria.CriterionEvaluatorRegistry;
import com.firstclub.membership.tiering.criteria.EvaluationContext;
import com.firstclub.membership.tiering.criteria.impl.CohortEvaluator;
import com.firstclub.membership.tiering.criteria.impl.MonthlyOrderValueEvaluator;
import com.firstclub.membership.tiering.criteria.impl.OrderCountEvaluator;
import com.firstclub.membership.tiering.criteria.impl.ReferralCountEvaluator;
import com.firstclub.membership.tiering.eligibility.TierEligibilityEvaluator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TierEligibilityEvaluatorTest {

    private final CriterionEvaluatorRegistry registry = new CriterionEvaluatorRegistry(List.of(
            new OrderCountEvaluator(),
            new MonthlyOrderValueEvaluator(),
            new ReferralCountEvaluator(),
            new CohortEvaluator()));
    private final TierEligibilityEvaluator evaluator = new TierEligibilityEvaluator(registry);

    private final Tier silver = tier("Silver", 0, CriteriaCombinator.ALL);
    private final Tier gold = goldTier();
    private final Tier platinum = platinumTier();
    private final List<Tier> tiers = List.of(silver, gold, platinum);

    @Test
    void earnsGoldOnOrderCount() {
        EvaluationContext ctx = context(null, 6, 0);
        assertThat(evaluator.freeEligibleTier(tiers, ctx)).isEqualTo(gold);
    }

    @Test
    void earnsGoldOnCohortWithNoOrders() {
        EvaluationContext ctx = context("VIP", 0, 0);
        assertThat(evaluator.freeEligibleTier(tiers, ctx)).isEqualTo(gold);
    }

    @Test
    void fallsBackToBaseWhenNothingMet() {
        EvaluationContext ctx = context(null, 0, 0);
        assertThat(evaluator.freeEligibleTier(tiers, ctx)).isEqualTo(silver);
    }

    @Test
    void earnsPlatinumOnHighOrderCount() {
        EvaluationContext ctx = context(null, 20, 0);
        assertThat(evaluator.freeEligibleTier(tiers, ctx)).isEqualTo(platinum);
    }

    private EvaluationContext context(String cohort, long orders, long value) {
        return new EvaluationContext(cohort, new ActivitySnapshot(orders, Money.ofRupees(value), 0));
    }

    private Tier tier(String name, int rank, CriteriaCombinator combinator) {
        Tier t = new Tier();
        t.setName(name);
        t.setRank(rank);
        t.setCriteriaCombinator(combinator);
        t.setMonthlyFee(Money.ZERO);
        t.setJoiningFee(Money.ZERO);
        return t;
    }

    private Tier goldTier() {
        Tier t = tier("Gold", 1, CriteriaCombinator.ANY);
        t.addCriterion(numeric(CriterionType.ORDER_COUNT, 5));
        t.addCriterion(cohort("VIP"));
        return t;
    }

    private Tier platinumTier() {
        Tier t = tier("Platinum", 2, CriteriaCombinator.ANY);
        t.addCriterion(numeric(CriterionType.ORDER_COUNT, 15));
        return t;
    }

    private CriterionConfig numeric(CriterionType type, int threshold) {
        CriterionConfig c = new CriterionConfig();
        c.setType(type);
        c.setOperator(ComparisonOperator.GTE);
        c.setThreshold(BigDecimal.valueOf(threshold));
        return c;
    }

    private CriterionConfig cohort(String value) {
        CriterionConfig c = new CriterionConfig();
        c.setType(CriterionType.COHORT);
        c.setStringValue(value);
        return c;
    }
}

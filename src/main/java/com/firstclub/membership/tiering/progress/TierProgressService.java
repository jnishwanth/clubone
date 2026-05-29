package com.firstclub.membership.tiering.progress;

import com.firstclub.membership.catalog.domain.CriterionConfig;
import com.firstclub.membership.catalog.domain.Tier;
import com.firstclub.membership.tiering.criteria.CriterionEvaluatorRegistry;
import com.firstclub.membership.tiering.criteria.EvaluationContext;
import com.firstclub.membership.tiering.eligibility.TierEligibilityEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/** Computes "progress to next tier" on read. Pure projection, no state change. */
@Service
@RequiredArgsConstructor
public class TierProgressService {

    private final CriterionEvaluatorRegistry registry;
    private final TierEligibilityEvaluator eligibilityEvaluator;

    public TierProgressView nextTierProgress(List<Tier> activeTiersAscending,
                                             EvaluationContext context,
                                             int currentRank) {
        Tier next = activeTiersAscending.stream()
                .filter(t -> t.getRank() > currentRank)
                .findFirst()
                .orElse(null);
        if (next == null) {
            return TierProgressView.atTop();
        }
        List<CriterionProgress> items = next.getQualifyingCriteria().stream()
                .map(c -> describe(c, context))
                .toList();
        boolean qualifies = eligibilityEvaluator.qualifiesFor(next, context);
        return new TierProgressView(false, next.getName(), next.getCriteriaCombinator(), qualifies, items);
    }

    private CriterionProgress describe(CriterionConfig config, EvaluationContext ctx) {
        boolean met = registry.get(config.getType()).evaluate(config, ctx);
        String requirement;
        String actual;
        switch (config.getType()) {
            case ORDER_COUNT -> {
                requirement = config.getOperator() + " " + config.getThreshold().toBigInteger() + " orders";
                actual = ctx.activity().orderCount() + " orders";
            }
            case MONTHLY_ORDER_VALUE -> {
                requirement = config.getOperator() + " " + config.getThreshold() + " spend";
                actual = ctx.activity().orderValue().amount() + " spent";
            }
            case REFERRAL_COUNT -> {
                requirement = config.getOperator() + " " + config.getThreshold().toBigInteger() + " referrals";
                actual = ctx.activity().referralCount() + " referrals";
            }
            case COHORT -> {
                requirement = "in cohort '" + config.getStringValue() + "'";
                actual = ctx.cohort() == null ? "no cohort" : "cohort '" + ctx.cohort() + "'";
            }
            default -> {
                requirement = config.getType().name();
                actual = "";
            }
        }
        return new CriterionProgress(config.getType(), requirement, actual, met);
    }
}

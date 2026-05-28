package com.firstclub.membership.benefit.application;

import com.firstclub.membership.catalog.domain.BenefitType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Spring picks up every BenefitApplier; we index by BenefitType. Same shape as
 *  {@code CriterionEvaluatorRegistry}. */
@Component
public class BenefitApplierRegistry {

    private final Map<BenefitType, BenefitApplier> byType;

    public BenefitApplierRegistry(List<BenefitApplier> appliers) {
        this.byType = appliers.stream()
                .collect(Collectors.toMap(BenefitApplier::type, Function.identity()));
    }

    public BenefitApplier get(BenefitType type) {
        BenefitApplier applier = byType.get(type);
        if (applier == null) {
            throw new IllegalStateException("No applier registered for benefit type " + type);
        }
        return applier;
    }
}

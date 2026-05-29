package com.firstclub.membership.tiering.criteria;

import com.firstclub.membership.catalog.domain.CriterionType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Spring picks up every CriterionEvaluator; we index by CriterionType for O(1) lookup. */
@Component
public class CriterionEvaluatorRegistry {

    private final Map<CriterionType, CriterionEvaluator> byType;

    public CriterionEvaluatorRegistry(List<CriterionEvaluator> evaluators) {
        this.byType = evaluators.stream()
                .collect(java.util.stream.Collectors.toMap(CriterionEvaluator::type, Function.identity()));
    }

    public CriterionEvaluator get(CriterionType type) {
        CriterionEvaluator evaluator = byType.get(type);
        if (evaluator == null) {
            throw new IllegalStateException("No evaluator registered for criterion type " + type);
        }
        return evaluator;
    }
}

package com.firstclub.membership.tiering.progress;

import com.firstclub.membership.catalog.domain.CriterionType;

/** One line of "progress to next tier": what's required, where the user is, met? */
public record CriterionProgress(
        CriterionType type,
        String requirement,
        String actual,
        boolean met
) {
}

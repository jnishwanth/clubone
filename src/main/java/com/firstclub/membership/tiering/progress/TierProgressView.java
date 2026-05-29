package com.firstclub.membership.tiering.progress;

import com.firstclub.membership.catalog.domain.CriteriaCombinator;

import java.util.List;

/** Read view of progress to the next tier. Computed live from current activity; doesn't
 *  mutate state. */
public record TierProgressView(
        boolean atTopTier,
        String nextTierName,
        CriteriaCombinator combinator,
        boolean qualifiesNow,
        List<CriterionProgress> criteria
) {

    public static TierProgressView atTop() {
        return new TierProgressView(true, null, null, false, List.of());
    }
}

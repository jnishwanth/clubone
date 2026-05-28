package com.firstclub.membership.tiering.settlement;

import com.firstclub.membership.catalog.domain.Tier;
import com.firstclub.membership.common.Money;

/**
 * Outcome of applying a settlement policy for one period.
 *
 * @param feeOwed       amount the user must pay to retain {@code tierIfPaid}; zero = waived
 * @param tierIfPaid    tier kept if the fee is paid (the held tier)
 * @param tierIfUnpaid  tier settled to if the fee is not paid, or immediately when waived
 *                      (the highest free-eligible tier)
 */
public record SettlementDecision(Money feeOwed, Tier tierIfPaid, Tier tierIfUnpaid) {

    public boolean feeRequired() {
        return !feeOwed.isZero();
    }
}

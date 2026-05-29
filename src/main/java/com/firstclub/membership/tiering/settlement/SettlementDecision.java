package com.firstclub.membership.tiering.settlement;

import com.firstclub.membership.catalog.domain.Tier;
import com.firstclub.membership.common.Money;

/** Output of the settlement policy. {@code feeOwed} zero = waived. {@code tierIfPaid} is
 *  the held tier; {@code tierIfUnpaid} is the free-eligible tier (also the immediate result
 *  when waived). */
public record SettlementDecision(Money feeOwed, Tier tierIfPaid, Tier tierIfUnpaid) {

    public boolean feeRequired() {
        return !feeOwed.isZero();
    }
}

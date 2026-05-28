package com.firstclub.membership.tiering.settlement;

import java.math.BigDecimal;
import java.time.Instant;

/** Read view of a settlement record (history / outstanding invoices). */
public record SettlementView(
        Long settlementId,
        String period,
        SettlementStatus status,
        BigDecimal feeOwed,
        Long heldTierId,
        Long freeEligibleTierId,
        Instant graceDeadline,
        Long paymentId
) {
    public static SettlementView from(TierSettlement s) {
        return new SettlementView(s.getId(), s.getPeriod(), s.getStatus(), s.getFeeOwed().amount(),
                s.getHeldTierId(), s.getFreeEligibleTierId(), s.getGraceDeadline(), s.getPaymentId());
    }
}

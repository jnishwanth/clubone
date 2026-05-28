package com.firstclub.membership.tiering.settlement;

/** Lifecycle of a single monthly tier settlement. */
public enum SettlementStatus {
    /** Criteria met (or already at/below free-eligible) — no fee, tier settled free. */
    WAIVED,
    /** Difference fee invoiced; awaiting payment within the grace window. */
    FEE_INVOICED,
    /** Fee paid in time — held tier retained. */
    PAID,
    /** Grace expired unpaid — tier settled down to the free-eligible tier. */
    DOWNGRADED
}

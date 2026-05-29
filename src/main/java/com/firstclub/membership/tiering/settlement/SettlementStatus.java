package com.firstclub.membership.tiering.settlement;

/** Lifecycle of one monthly tier settlement. */
public enum SettlementStatus {
    /** No fee, tier settled free. */
    WAIVED,
    /** Fee invoiced, awaiting payment within the grace window. */
    FEE_INVOICED,
    /** Fee paid in time, held tier retained. */
    PAID,
    /** Grace expired unpaid, tier dropped to free-eligible. */
    DOWNGRADED
}

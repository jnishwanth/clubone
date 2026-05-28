package com.firstclub.membership.catalog.domain;

/**
 * The metrics a tier's qualifying criteria can test. Each type maps to a
 * Strategy evaluator (see tiering slice) via a registry keyed by this enum, so a
 * new criterion kind = one evaluator class + one constant, no engine changes.
 */
public enum CriterionType {
    /** Number of fulfilled orders in the evaluation period. */
    ORDER_COUNT,
    /** Total order value in the evaluation period. */
    MONTHLY_ORDER_VALUE,
    /** Number of successful referrals in the period. */
    REFERRAL_COUNT,
    /** Membership of a configured cohort (string match, period-independent). */
    COHORT
}

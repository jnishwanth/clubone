package com.firstclub.membership.payment.domain;

public enum PaymentPurpose {
    /** Base subscription (plan) fee. */
    SUBSCRIPTION,
    /** Upfront fee to join a higher tier immediately. */
    TIER_JOINING,
    /** Recurring monthly maintenance fee (difference) when criteria aren't met. */
    TIER_MAINTENANCE
}

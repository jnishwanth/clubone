package com.firstclub.membership.catalog.domain;

/** Subscription billing cadence. Carries its length so expiry is derivable. */
public enum BillingPeriod {
    MONTHLY(1),
    QUARTERLY(3),
    YEARLY(12);

    private final int months;

    BillingPeriod(int months) {
        this.months = months;
    }

    public int months() {
        return months;
    }
}

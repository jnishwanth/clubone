package com.firstclub.membership.catalog.domain;

/**
 * Kinds of perk a tier can grant. {@code valued} types use the per-tier numeric
 * value (e.g. discount percent); the rest are simple on/off entitlements.
 * Adding a new perk = one enum constant (+ handling in the benefit-preview).
 */
public enum BenefitType {
    FREE_DELIVERY(false),
    EXTRA_DISCOUNT_PERCENT(true),
    EXCLUSIVE_DEALS(false),
    PRIORITY_SUPPORT(false);

    private final boolean valued;

    BenefitType(boolean valued) {
        this.valued = valued;
    }

    /** Whether this benefit reads the per-tier numeric value. */
    public boolean isValued() {
        return valued;
    }
}

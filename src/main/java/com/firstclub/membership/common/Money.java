package com.firstclub.membership.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Immutable money value object. Single-currency (INR) by design — encapsulates
 * scale/rounding so money is never passed around as a raw double/BigDecimal.
 * Persisted as a single numeric column via {@link MoneyConverter}.
 */
public record Money(BigDecimal amount) implements Comparable<Money> {

    public static final Money ZERO = Money.of(BigDecimal.ZERO);

    public Money {
        Objects.requireNonNull(amount, "amount");
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public static Money ofRupees(long rupees) {
        return new Money(BigDecimal.valueOf(rupees));
    }

    public Money plus(Money other) {
        return new Money(amount.add(other.amount));
    }

    public Money minus(Money other) {
        return new Money(amount.subtract(other.amount));
    }

    /** Returns the given percentage of this amount (e.g. percentOf(10) = 10%). */
    public Money percentOf(BigDecimal percent) {
        return new Money(amount.multiply(percent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
    }

    public Money max(Money other) {
        return amount.compareTo(other.amount) >= 0 ? this : other;
    }

    /** Floors at zero — a fee difference can never be negative. */
    public Money atLeastZero() {
        return isNegative() ? ZERO : this;
    }

    public boolean isNegative() {
        return amount.signum() < 0;
    }

    public boolean isZero() {
        return amount.signum() == 0;
    }

    public boolean isGreaterThan(Money other) {
        return amount.compareTo(other.amount) > 0;
    }

    @Override
    public int compareTo(Money other) {
        return amount.compareTo(other.amount);
    }
}

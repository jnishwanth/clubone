package com.firstclub.membership.activity.domain;

import com.firstclub.membership.common.Money;

/**
 * Immutable view of a user's metrics for one period, handed to the tiering engine.
 * Decouples the engine from the activity persistence layer.
 */
public record ActivitySnapshot(long orderCount, Money orderValue, int referralCount) {

    public static ActivitySnapshot zero() {
        return new ActivitySnapshot(0L, Money.ZERO, 0);
    }
}

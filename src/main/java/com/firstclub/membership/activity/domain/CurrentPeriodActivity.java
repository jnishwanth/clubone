package com.firstclub.membership.activity.domain;

import com.firstclub.membership.common.BaseEntity;
import com.firstclub.membership.common.Money;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Per-user, per-period accumulation of order activity — a local read model fed by
 * order events. One row per (user, period); a new month is simply a new row, so
 * there is no destructive monthly reset (and no reset race). Counters are bumped
 * via an atomic SQL UPDATE (see repository), not read-modify-write.
 */
@Entity
@Table(name = "current_period_activity",
        uniqueConstraints = @UniqueConstraint(name = "uk_activity_user_period",
                columnNames = {"user_id", "period"}))
@Getter
@Setter
@NoArgsConstructor
public class CurrentPeriodActivity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** Evaluation period as "YYYY-MM". */
    @Column(nullable = false)
    private String period;

    @Column(name = "order_count", nullable = false)
    private long orderCount;

    @Column(name = "order_value", nullable = false)
    private Money orderValue = Money.ZERO;

    @Column(name = "referral_count", nullable = false)
    private int referralCount;

    public CurrentPeriodActivity(Long userId, String period, long orderCount, Money orderValue, int referralCount) {
        this.userId = userId;
        this.period = period;
        this.orderCount = orderCount;
        this.orderValue = orderValue;
        this.referralCount = referralCount;
    }
}

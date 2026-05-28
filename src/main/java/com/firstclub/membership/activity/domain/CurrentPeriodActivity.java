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

/** One row per (user, period). New month = new row, so there's no destructive reset and no
 *  reset race. Counters bumped via atomic SQL UPDATE in the repo, not read-modify-write. */
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

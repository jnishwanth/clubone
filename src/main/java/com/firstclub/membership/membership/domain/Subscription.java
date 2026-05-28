package com.firstclub.membership.membership.domain;

import com.firstclub.membership.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * A user's membership: a plan + the tier currently held. {@code @Version} (from
 * BaseEntity) gives optimistic locking so a concurrent upgrade and a settlement
 * run can't silently clobber each other. References plan/tier by id to keep slices
 * decoupled.
 */
@Entity
@Table(name = "subscriptions",
        indexes = @Index(name = "idx_subscription_status_expiry", columnList = "status, expiry_date"))
@Getter
@Setter
@NoArgsConstructor
public class Subscription extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "held_tier_id", nullable = false)
    private Long heldTierId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE;
    }
}

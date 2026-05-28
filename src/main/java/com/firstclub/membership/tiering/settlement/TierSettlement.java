package com.firstclub.membership.tiering.settlement;

import com.firstclub.membership.common.BaseEntity;
import com.firstclub.membership.common.Money;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/** One row per (subscription, period). The unique constraint is the idempotency key —
 *  a re-run or overlapping manual trigger can't double-charge. Other aggregates referenced
 *  by id only, no JPA relationships, so tiering stays decoupled. */
@Entity
@Table(name = "tier_settlements",
        uniqueConstraints = @UniqueConstraint(name = "uk_settlement_subscription_period",
                columnNames = {"subscription_id", "period"}))
@Getter
@Setter
@NoArgsConstructor
public class TierSettlement extends BaseEntity {

    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;

    @Column(nullable = false)
    private String period;

    @Column(name = "held_tier_id", nullable = false)
    private Long heldTierId;

    @Column(name = "free_eligible_tier_id", nullable = false)
    private Long freeEligibleTierId;

    @Column(name = "fee_owed", nullable = false)
    private Money feeOwed = Money.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementStatus status;

    /** Payment deadline for FEE_INVOICED settlements; null when waived. */
    private Instant graceDeadline;

    /** Linked payment when a fee was invoiced. */
    @Column(name = "payment_id")
    private Long paymentId;
}

package com.firstclub.membership.tiering.settlement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TierSettlementRepository extends JpaRepository<TierSettlement, Long> {

    boolean existsBySubscriptionIdAndPeriod(Long subscriptionId, String period);

    Optional<TierSettlement> findBySubscriptionIdAndPeriod(Long subscriptionId, String period);

    Optional<TierSettlement> findByPaymentId(Long paymentId);

    List<TierSettlement> findByStatusAndGraceDeadlineBefore(SettlementStatus status, Instant cutoff);

    List<TierSettlement> findBySubscriptionIdOrderByPeriodDesc(Long subscriptionId);

    /** CAS: only flips a settlement still at FEE_INVOICED. Payment-confirm vs grace-sweep,
     *  the loser updates 0 rows and no-ops. Atomic at the row level, no explicit lock. */
    @Modifying
    @Query("update TierSettlement s set s.status = :target "
            + "where s.id = :id and s.status = com.firstclub.membership.tiering.settlement.SettlementStatus.FEE_INVOICED")
    int transitionFromInvoiced(@Param("id") Long id, @Param("target") SettlementStatus target);
}

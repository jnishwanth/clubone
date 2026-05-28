package com.firstclub.membership.membership.repository;

import com.firstclub.membership.membership.domain.Subscription;
import com.firstclub.membership.membership.domain.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);

    boolean existsByUserIdAndStatus(Long userId, SubscriptionStatus status);

    List<Subscription> findByStatus(SubscriptionStatus status);

    /**
     * Set-based expiry: flip every lapsed ACTIVE subscription to EXPIRED in one
     * statement. The {@code status = ACTIVE} guard makes it idempotent and avoids
     * racing a concurrent cancel. Bulk updates bypass dirty-checking, so updatedAt
     * is set explicitly. Returns the number expired.
     */
    @Modifying
    @Query("update Subscription s "
            + "set s.status = com.firstclub.membership.membership.domain.SubscriptionStatus.EXPIRED, "
            + "s.updatedAt = :now "
            + "where s.status = com.firstclub.membership.membership.domain.SubscriptionStatus.ACTIVE "
            + "and s.expiryDate < :today")
    int expireActiveBefore(@Param("today") LocalDate today, @Param("now") Instant now);
}

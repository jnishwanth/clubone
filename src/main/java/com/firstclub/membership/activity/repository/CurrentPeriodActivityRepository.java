package com.firstclub.membership.activity.repository;

import com.firstclub.membership.activity.domain.CurrentPeriodActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface CurrentPeriodActivityRepository extends JpaRepository<CurrentPeriodActivity, Long> {

    Optional<CurrentPeriodActivity> findByUserIdAndPeriod(Long userId, String period);

    /**
     * Atomic increment of one user's period counters. A pure increment needs no
     * prior read, so a single SQL UPDATE eliminates the lost-update race with zero
     * retries — preferred over optimistic locking (retry churn) or pessimistic
     * locks (needless serialization). Returns rows affected (0 = row not yet there).
     */
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE current_period_activity "
            + "SET order_count = order_count + 1, order_value = order_value + :amount "
            + "WHERE user_id = :userId AND period = :period", nativeQuery = true)
    int incrementOrder(@Param("userId") Long userId,
                       @Param("period") String period,
                       @Param("amount") BigDecimal amount);
}

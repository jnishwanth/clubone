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

    /** Atomic increment of the period's counters. Single SQL UPDATE so there's no read step
     *  for concurrent orders to race on. Returns rows affected (0 if the row's not there). */
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE current_period_activity "
            + "SET order_count = order_count + 1, order_value = order_value + :amount "
            + "WHERE user_id = :userId AND period = :period", nativeQuery = true)
    int incrementOrder(@Param("userId") Long userId,
                       @Param("period") String period,
                       @Param("amount") BigDecimal amount);
}

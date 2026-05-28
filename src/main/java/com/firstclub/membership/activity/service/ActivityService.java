package com.firstclub.membership.activity.service;

import com.firstclub.membership.activity.domain.ActivitySnapshot;
import com.firstclub.membership.activity.domain.OrderFulfilledEvent;
import com.firstclub.membership.activity.repository.CurrentPeriodActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

/** Ingests order events into the read model; exposes period metrics to the tiering engine. */
@Service
@RequiredArgsConstructor
public class ActivityService {

    private final CurrentPeriodActivityRepository repository;
    private final ActivityRowInitializer rowInitializer;

    /** Atomic increment first; if row doesn't exist yet, insert in its own tx and increment
     *  again. Keeps concurrent orders correct for the same user without locking. */
    @Transactional
    public void applyOrderFulfilled(OrderFulfilledEvent event) {
        // Happy path: row exists, single atomic UPDATE.
        int updated = repository.incrementOrder(event.userId(), event.period(), event.amount());
        if (updated == 0) {
            // First order for this period — let the row initializer try to create it.
            try {
                rowInitializer.insertFirstOrder(event.userId(), event.period(), event.amount());
            } catch (DataIntegrityViolationException raceLost) {
                // Lost the race — another thread inserted first. Row exists now; increment applies.
                repository.incrementOrder(event.userId(), event.period(), event.amount());
            }
        }
    }

    @Transactional(readOnly = true)
    public ActivitySnapshot snapshot(Long userId, YearMonth period) {
        return repository.findByUserIdAndPeriod(userId, period.toString())
                .map(a -> new ActivitySnapshot(a.getOrderCount(), a.getOrderValue(), a.getReferralCount()))
                .orElseGet(ActivitySnapshot::zero);
    }
}

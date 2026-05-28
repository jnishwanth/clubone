package com.firstclub.membership.activity.service;

import com.firstclub.membership.activity.domain.ActivitySnapshot;
import com.firstclub.membership.activity.domain.OrderFulfilledEvent;
import com.firstclub.membership.activity.repository.CurrentPeriodActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

/**
 * Public API of the activity slice: ingest order events into the read model, and
 * expose period metrics to the tiering engine.
 */
@Service
@RequiredArgsConstructor
public class ActivityService {

    private final CurrentPeriodActivityRepository repository;
    private final ActivityRowInitializer rowInitializer;

    /**
     * Apply an order: atomic increment first; if the row doesn't exist yet, create
     * it (in a separate transaction) and increment again. This keeps concurrent
     * orders for the same user correct without locking.
     */
    @Transactional
    public void applyOrderFulfilled(OrderFulfilledEvent event) {
        int updated = repository.incrementOrder(event.userId(), event.period(), event.amount());
        if (updated == 0) {
            try {
                rowInitializer.insertFirstOrder(event.userId(), event.period(), event.amount());
            } catch (DataIntegrityViolationException raceLost) {
                // A concurrent insert created the row first — the increment now applies.
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

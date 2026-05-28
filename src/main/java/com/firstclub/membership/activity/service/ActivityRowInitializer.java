package com.firstclub.membership.activity.service;

import com.firstclub.membership.activity.domain.CurrentPeriodActivity;
import com.firstclub.membership.activity.repository.CurrentPeriodActivityRepository;
import com.firstclub.membership.common.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Creates the first activity row for a (user, period) in its OWN transaction
 * (REQUIRES_NEW). Isolating the insert means a unique-constraint violation from a
 * concurrent insert rolls back only this inner transaction — the caller's
 * transaction stays healthy and can simply retry the atomic increment.
 */
@Component
@RequiredArgsConstructor
public class ActivityRowInitializer {

    private final CurrentPeriodActivityRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertFirstOrder(Long userId, String period, BigDecimal amount) {
        // saveAndFlush forces the constraint check inside THIS transaction, so a
        // losing race surfaces here as DataIntegrityViolationException (caller catches).
        repository.saveAndFlush(new CurrentPeriodActivity(userId, period, 1L, Money.of(amount), 0));
    }
}

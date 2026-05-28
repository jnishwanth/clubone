package com.firstclub.membership.activity.service;

import com.firstclub.membership.activity.domain.CurrentPeriodActivity;
import com.firstclub.membership.activity.repository.CurrentPeriodActivityRepository;
import com.firstclub.membership.common.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/** Inserts the first activity row for a (user, period) in its OWN tx so a unique-constraint
 *  race rolls back only the inner tx — the caller's tx stays clean and just retries the
 *  increment. */
@Component
@RequiredArgsConstructor
public class ActivityRowInitializer {

    private final CurrentPeriodActivityRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertFirstOrder(Long userId, String period, BigDecimal amount) {
        // saveAndFlush — force the unique-key check inside this tx, so a race surfaces as
        // DataIntegrityViolationException for the caller to catch.
        repository.saveAndFlush(new CurrentPeriodActivity(userId, period, 1L, Money.of(amount), 0));
    }
}

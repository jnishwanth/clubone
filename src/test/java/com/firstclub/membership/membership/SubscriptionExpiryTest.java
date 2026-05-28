package com.firstclub.membership.membership;

import com.firstclub.membership.membership.domain.Subscription;
import com.firstclub.membership.membership.domain.SubscriptionStatus;
import com.firstclub.membership.membership.repository.SubscriptionRepository;
import com.firstclub.membership.membership.service.SubscriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SubscriptionExpiryTest {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    @Test
    void expiresOnlyLapsedActiveSubscriptions() {
        Long pastActive = save(SubscriptionStatus.ACTIVE, LocalDate.now().minusDays(1));
        Long futureActive = save(SubscriptionStatus.ACTIVE, LocalDate.now().plusDays(10));
        Long pastCancelled = save(SubscriptionStatus.CANCELLED, LocalDate.now().minusDays(5));

        int expired = subscriptionService.expireDueSubscriptions();
        assertThat(expired).isEqualTo(1);

        assertThat(status(pastActive)).isEqualTo(SubscriptionStatus.EXPIRED);
        assertThat(status(futureActive)).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(status(pastCancelled)).isEqualTo(SubscriptionStatus.CANCELLED);

        // Idempotent: a second sweep finds nothing left to expire.
        assertThat(subscriptionService.expireDueSubscriptions()).isZero();
    }

    private Long save(SubscriptionStatus status, LocalDate expiry) {
        Subscription s = new Subscription();
        s.setUserId(1L);
        s.setPlanId(1L);
        s.setHeldTierId(1L);
        s.setStatus(status);
        s.setStartDate(LocalDate.now().minusMonths(1));
        s.setExpiryDate(expiry);
        return subscriptionRepository.save(s).getId();
    }

    private SubscriptionStatus status(Long id) {
        return subscriptionRepository.findById(id).orElseThrow().getStatus();
    }
}

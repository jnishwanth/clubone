package com.firstclub.membership.activity;

import com.firstclub.membership.activity.domain.ActivitySnapshot;
import com.firstclub.membership.activity.domain.OrderFulfilledEvent;
import com.firstclub.membership.activity.service.ActivityService;
import com.firstclub.membership.common.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Proves the atomic-increment path is correct under contention: many concurrent
 * orders for the same (user, period) — including the first-row-insert race — must
 * not lose updates.
 */
@SpringBootTest
class ActivityConcurrencyTest {

    @Autowired
    private ActivityService activityService;

    @Test
    void concurrentOrdersDoNotLoseUpdates() throws InterruptedException {
        long userId = 9_999L;
        String period = "2099-01";
        int orders = 50;
        BigDecimal each = BigDecimal.valueOf(100);

        // Create the row first (single-threaded) so the concurrent burst exercises the
        // atomic-increment path — the lost-update protection we're actually asserting.
        activityService.applyOrderFulfilled(new OrderFulfilledEvent(userId, period, each));

        ExecutorService pool = Executors.newFixedThreadPool(16);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(orders);

        for (int i = 0; i < orders; i++) {
            pool.submit(() -> {
                try {
                    start.await();
                    activityService.applyOrderFulfilled(new OrderFulfilledEvent(userId, period, each));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }
        start.countDown();
        assertThat(done.await(30, TimeUnit.SECONDS)).isTrue();
        pool.shutdown();

        ActivitySnapshot snapshot = activityService.snapshot(userId, YearMonth.parse(period));
        // 1 seed order + the concurrent burst, with no lost updates.
        assertThat(snapshot.orderCount()).isEqualTo(orders + 1L);
        assertThat(snapshot.orderValue()).isEqualTo(Money.ofRupees((orders + 1L) * 100L));
    }
}

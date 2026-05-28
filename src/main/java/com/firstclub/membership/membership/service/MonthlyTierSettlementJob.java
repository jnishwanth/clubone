package com.firstclub.membership.membership.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

/**
 * Scheduled command side: the monthly job settles the just-completed month; daily
 * jobs downgrade lapsed grace windows and expire subscriptions past their period.
 * Reads return live data separately (CQRS-flavored: events accumulate, the schedule settles).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MonthlyTierSettlementJob {

    private final TierSettlementOrchestrator orchestrator;
    private final SubscriptionService subscriptionService;

    @Scheduled(cron = "${membership.evaluation-cron}")
    public void runMonthlySettlement() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        int count = orchestrator.runMonthlySettlement(lastMonth);
        log.info("Monthly settlement for {} processed {} subscriptions.", lastMonth, count);
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void sweepGrace() {
        int count = orchestrator.sweepExpiredGrace();
        if (count > 0) {
            log.info("Grace sweep downgraded {} subscriptions.", count);
        }
    }

    @Scheduled(cron = "0 30 3 * * *")
    public void expireSubscriptions() {
        int count = subscriptionService.expireDueSubscriptions();
        if (count > 0) {
            log.info("Expiry sweep expired {} subscriptions.", count);
        }
    }
}

package com.firstclub.membership.membership.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

/** Scheduled triggers: monthly settlement, daily grace sweep, daily expiry sweep.
 *  All three crons come from application.yml so ops can retune without code. */
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

    @Scheduled(cron = "${membership.grace-sweep-cron}")
    public void sweepGrace() {
        int count = orchestrator.sweepExpiredGrace();
        if (count > 0) {
            log.info("Grace sweep downgraded {} subscriptions.", count);
        }
    }

    @Scheduled(cron = "${membership.expiry-sweep-cron}")
    public void expireSubscriptions() {
        int count = subscriptionService.expireDueSubscriptions();
        if (count > 0) {
            log.info("Expiry sweep expired {} subscriptions.", count);
        }
    }
}

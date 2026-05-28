package com.firstclub.membership.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Operational knobs from application.yml. Business policy (fees, thresholds, grace window)
 *  intentionally lives in the DB so it can change at runtime via admin APIs. */
@Data
@ConfigurationProperties(prefix = "membership")
public class MembershipProperties {

    /** Default display currency. Single-currency by design. */
    private String defaultCurrency = "INR";

    /** Seed default catalog config on startup when the tables are empty. */
    private boolean seedOnStartup = true;

    /** Cron for the monthly tier-settlement job. */
    private String evaluationCron = "0 0 2 1 * *";

    /** Cron for the daily grace-window sweep (downgrades unpaid fee invoices). */
    private String graceSweepCron = "0 0 3 * * *";

    /** Cron for the daily expiry sweep (flips lapsed ACTIVE subscriptions to EXPIRED). */
    private String expirySweepCron = "0 30 3 * * *";

    private Payment payment = new Payment();

    @Data
    public static class Payment {
        /** Mock gateway: auto-approve charges so the happy path is demoable. */
        private boolean autoApprove = true;
    }
}

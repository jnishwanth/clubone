package com.firstclub.membership.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Bucket-B configuration: operational/infra knobs bound from application.yml.
 * Type-safe and validated — no scattered {@code @Value} strings or magic constants.
 * Business-tunable policy (fees, thresholds, grace window) deliberately does NOT
 * live here; it lives in the DB so it can change at runtime via admin APIs.
 */
@Data
@ConfigurationProperties(prefix = "membership")
public class MembershipProperties {

    /** Default display currency. Single-currency by design. */
    private String defaultCurrency = "INR";

    /** Seed default catalog config on startup when the tables are empty. */
    private boolean seedOnStartup = true;

    /** Cron for the monthly tier-settlement job. */
    private String evaluationCron = "0 0 2 1 * *";

    private Payment payment = new Payment();

    @Data
    public static class Payment {
        /** Mock gateway: auto-approve charges so the happy path is demoable. */
        private boolean autoApprove = true;
    }
}

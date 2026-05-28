package com.firstclub.membership.membership.web.dto;

import com.firstclub.membership.catalog.domain.BillingPeriod;
import com.firstclub.membership.catalog.web.dto.BenefitDto;
import com.firstclub.membership.membership.domain.SubscriptionStatus;
import com.firstclub.membership.tiering.progress.TierProgressView;

import java.time.LocalDate;
import java.util.List;

/** What a user sees about their membership: plan, tier, expiry, benefits, progress. */
public record MembershipView(
        Long subscriptionId,
        Long userId,
        String planName,
        BillingPeriod billingPeriod,
        SubscriptionStatus status,
        LocalDate startDate,
        LocalDate expiryDate,
        String tierName,
        int tierRank,
        List<BenefitDto> benefits,
        TierProgressView progressToNextTier
) {
}

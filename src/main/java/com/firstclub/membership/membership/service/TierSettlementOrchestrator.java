package com.firstclub.membership.membership.service;

import com.firstclub.membership.membership.domain.Subscription;
import com.firstclub.membership.membership.domain.SubscriptionStatus;
import com.firstclub.membership.membership.repository.SubscriptionRepository;
import com.firstclub.membership.payment.domain.Payment;
import com.firstclub.membership.payment.service.PaymentService;
import com.firstclub.membership.tiering.settlement.TierSettlement;
import com.firstclub.membership.tiering.settlement.TierSettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.YearMonth;
import java.util.List;

/**
 * Coordinates settlement across subscriptions (command side). Delegates per-item
 * work to {@link SubscriptionSettler} so each runs in its own transaction.
 */
@Service
@RequiredArgsConstructor
public class TierSettlementOrchestrator {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionSettler settler;
    private final TierSettlementService settlementService;
    private final PaymentService paymentService;

    /** Settle every active subscription for the given period. Returns count processed. */
    public int runMonthlySettlement(YearMonth period) {
        List<Subscription> active = subscriptionRepository.findByStatus(SubscriptionStatus.ACTIVE);
        String periodKey = period.toString();
        active.forEach(s -> settler.settle(s.getId(), periodKey));
        return active.size();
    }

    /** Downgrade subscriptions whose grace window lapsed unpaid. Returns count swept. */
    public int sweepExpiredGrace() {
        List<TierSettlement> expired = settlementService.findExpiredInvoices(Instant.now());
        expired.forEach(s -> settler.downgradeForExpiredGrace(s.getId(), s.getSubscriptionId(), s.getFreeEligibleTierId()));
        return expired.size();
    }

    /** Confirm (pay) a maintenance-fee invoice; on success the held tier is retained. */
    @Transactional
    public Payment confirmFeePayment(Long paymentId) {
        Payment payment = paymentService.confirm(paymentId);
        settlementService.markPaidForPayment(paymentId);
        return payment;
    }
}

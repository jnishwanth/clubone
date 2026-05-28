package com.firstclub.membership.tiering.settlement;

import com.firstclub.membership.common.Money;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Records and transitions {@link TierSettlement}s. Pure bookkeeping over a
 * subscription id (no dependency on the membership slice). Idempotent inserts and
 * compare-and-set transitions keep it safe under retries and concurrent triggers.
 */
@Service
@RequiredArgsConstructor
public class TierSettlementService {

    private final TierSettlementRepository repository;

    @Transactional
    public TierSettlement recordWaived(Long subscriptionId, String period, Long heldTierId, Long freeEligibleTierId) {
        return repository.findBySubscriptionIdAndPeriod(subscriptionId, period).orElseGet(() -> {
            TierSettlement s = new TierSettlement();
            s.setSubscriptionId(subscriptionId);
            s.setPeriod(period);
            s.setHeldTierId(heldTierId);
            s.setFreeEligibleTierId(freeEligibleTierId);
            s.setFeeOwed(Money.ZERO);
            s.setStatus(SettlementStatus.WAIVED);
            return repository.save(s);
        });
    }

    @Transactional
    public TierSettlement recordInvoiced(Long subscriptionId, String period, Long heldTierId,
                                         Long freeEligibleTierId, Money feeOwed,
                                         Instant graceDeadline, Long paymentId) {
        return repository.findBySubscriptionIdAndPeriod(subscriptionId, period).orElseGet(() -> {
            TierSettlement s = new TierSettlement();
            s.setSubscriptionId(subscriptionId);
            s.setPeriod(period);
            s.setHeldTierId(heldTierId);
            s.setFreeEligibleTierId(freeEligibleTierId);
            s.setFeeOwed(feeOwed);
            s.setStatus(SettlementStatus.FEE_INVOICED);
            s.setGraceDeadline(graceDeadline);
            s.setPaymentId(paymentId);
            return repository.save(s);
        });
    }

    /** Compare-and-set FEE_INVOICED → PAID. Returns true if this call made the change. */
    @Transactional
    public boolean markPaid(Long settlementId) {
        return repository.transitionFromInvoiced(settlementId, SettlementStatus.PAID) > 0;
    }

    /** Compare-and-set FEE_INVOICED → DOWNGRADED. Returns true if this call made the change. */
    @Transactional
    public boolean markDowngraded(Long settlementId) {
        return repository.transitionFromInvoiced(settlementId, SettlementStatus.DOWNGRADED) > 0;
    }

    /** Mark the settlement linked to a paid invoice as PAID. Returns true if transitioned. */
    @Transactional
    public boolean markPaidForPayment(Long paymentId) {
        return repository.findByPaymentId(paymentId)
                .map(s -> repository.transitionFromInvoiced(s.getId(), SettlementStatus.PAID) > 0)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean alreadySettled(Long subscriptionId, String period) {
        return repository.existsBySubscriptionIdAndPeriod(subscriptionId, period);
    }

    @Transactional(readOnly = true)
    public Optional<TierSettlement> findOpenInvoice(Long subscriptionId, String period) {
        return repository.findBySubscriptionIdAndPeriod(subscriptionId, period)
                .filter(s -> s.getStatus() == SettlementStatus.FEE_INVOICED);
    }

    @Transactional(readOnly = true)
    public List<TierSettlement> findExpiredInvoices(Instant cutoff) {
        return repository.findByStatusAndGraceDeadlineBefore(SettlementStatus.FEE_INVOICED, cutoff);
    }

    @Transactional(readOnly = true)
    public List<SettlementView> history(Long subscriptionId) {
        return repository.findBySubscriptionIdOrderByPeriodDesc(subscriptionId).stream()
                .map(SettlementView::from)
                .toList();
    }
}

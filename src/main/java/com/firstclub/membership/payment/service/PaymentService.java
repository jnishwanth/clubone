package com.firstclub.membership.payment.service;

import com.firstclub.membership.common.BusinessRuleException;
import com.firstclub.membership.common.Money;
import com.firstclub.membership.common.NotFoundException;
import com.firstclub.membership.payment.domain.Payment;
import com.firstclub.membership.payment.domain.PaymentGateway;
import com.firstclub.membership.payment.domain.PaymentPurpose;
import com.firstclub.membership.payment.domain.PaymentStatus;
import com.firstclub.membership.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Owns the payment lifecycle. {@code chargeNow} settles immediately (subscribe/joining);
 *  {@code createInvoice} records an unpaid fee the user confirms later. Mock gateway is
 *  in-process so charging inside the tx is fine here; with a real gateway you'd persist
 *  intent, commit, then charge. */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentGateway gateway;

    @Transactional
    public Payment chargeNow(Long userId, Long subscriptionId, Money amount, PaymentPurpose purpose) {
        Payment payment = newPayment(userId, subscriptionId, amount, purpose);
        PaymentGateway.ChargeOutcome outcome = gateway.charge(amount);
        payment.setGatewayReference(outcome.reference());
        if (!outcome.success()) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new BusinessRuleException("Payment declined for " + purpose);
        }
        payment.setStatus(PaymentStatus.SUCCESS);
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment createInvoice(Long userId, Long subscriptionId, Money amount, PaymentPurpose purpose) {
        return paymentRepository.save(newPayment(userId, subscriptionId, amount, purpose));
    }

    /** Confirm (pay) a pending invoice. Idempotent if already settled. */
    @Transactional
    public Payment confirm(Long paymentId) {
        Payment payment = require(paymentId);
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return payment;
        }
        PaymentGateway.ChargeOutcome outcome = gateway.charge(payment.getAmount());
        payment.setGatewayReference(outcome.reference());
        if (!outcome.success()) {
            throw new BusinessRuleException("Payment declined for payment " + paymentId);
        }
        payment.setStatus(PaymentStatus.SUCCESS);
        return paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public Payment require(Long id) {
        return paymentRepository.findById(id).orElseThrow(() -> NotFoundException.of("Payment", id));
    }

    private Payment newPayment(Long userId, Long subscriptionId, Money amount, PaymentPurpose purpose) {
        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setSubscriptionId(subscriptionId);
        payment.setAmount(amount);
        payment.setPurpose(purpose);
        payment.setStatus(PaymentStatus.PENDING);
        return payment;
    }
}

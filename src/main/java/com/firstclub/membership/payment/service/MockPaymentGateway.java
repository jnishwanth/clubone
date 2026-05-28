package com.firstclub.membership.payment.service;

import com.firstclub.membership.common.Money;
import com.firstclub.membership.config.MembershipProperties;
import com.firstclub.membership.payment.domain.PaymentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mock gateway: outcome controlled by {@code membership.payment.auto-approve} so
 * both the "paid → kept" and "declined" branches are demoable.
 */
@Component
@RequiredArgsConstructor
public class MockPaymentGateway implements PaymentGateway {

    private final MembershipProperties properties;

    @Override
    public ChargeOutcome charge(Money amount) {
        boolean success = properties.getPayment().isAutoApprove();
        return new ChargeOutcome(success, "MOCK-" + UUID.randomUUID());
    }
}

package com.firstclub.membership.payment.web.dto;

import com.firstclub.membership.payment.domain.Payment;
import com.firstclub.membership.payment.domain.PaymentPurpose;
import com.firstclub.membership.payment.domain.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponse(
        Long id,
        Long userId,
        Long subscriptionId,
        BigDecimal amount,
        PaymentPurpose purpose,
        PaymentStatus status,
        String gatewayReference
) {
    public static PaymentResponse from(Payment p) {
        return new PaymentResponse(p.getId(), p.getUserId(), p.getSubscriptionId(),
                p.getAmount().amount(), p.getPurpose(), p.getStatus(), p.getGatewayReference());
    }
}

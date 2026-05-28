package com.firstclub.membership.payment.domain;

import com.firstclub.membership.common.Money;

/**
 * Port to a payment provider (Adapter). The domain depends on this interface, not
 * a concrete gateway, so a real provider can replace the mock with no other changes.
 */
public interface PaymentGateway {

    ChargeOutcome charge(Money amount);

    record ChargeOutcome(boolean success, String reference) {
    }
}

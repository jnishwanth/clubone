package com.firstclub.membership.membership.web;

import com.firstclub.membership.membership.service.TierSettlementOrchestrator;
import com.firstclub.membership.payment.service.PaymentService;
import com.firstclub.membership.payment.web.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BillingController {

    private final PaymentService paymentService;
    private final TierSettlementOrchestrator orchestrator;

    @GetMapping("/api/payments/{id}")
    public PaymentResponse get(@PathVariable Long id) {
        return PaymentResponse.from(paymentService.require(id));
    }

    /** Pay a maintenance-fee invoice within the grace window to retain the tier. */
    @PostMapping("/api/payments/{id}/confirm")
    public PaymentResponse confirm(@PathVariable Long id) {
        return PaymentResponse.from(orchestrator.confirmFeePayment(id));
    }
}

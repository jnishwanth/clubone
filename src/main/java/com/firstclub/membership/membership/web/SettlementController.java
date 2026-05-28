package com.firstclub.membership.membership.web;

import com.firstclub.membership.tiering.settlement.SettlementView;
import com.firstclub.membership.tiering.settlement.TierSettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Read-only settlement history for a subscription (find outstanding invoices). */
@RestController
@RequiredArgsConstructor
public class SettlementController {

    private final TierSettlementService settlementService;

    @GetMapping("/api/subscriptions/{id}/settlements")
    public List<SettlementView> history(@PathVariable Long id) {
        return settlementService.history(id);
    }
}

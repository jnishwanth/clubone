package com.firstclub.membership.membership.web;

import com.firstclub.membership.membership.service.TierSettlementOrchestrator;
import com.firstclub.membership.membership.web.dto.GraceSweepResponse;
import com.firstclub.membership.membership.web.dto.SettlementRunResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

/** Manual triggers so settlement and grace-expiry can be demoed without waiting. */
@RestController
@RequiredArgsConstructor
public class TieringAdminController {

    private final TierSettlementOrchestrator orchestrator;

    /** Run settlement for a period (default: current month). */
    @PostMapping("/api/admin/tiering/run")
    public SettlementRunResponse run(@RequestParam(required = false) String period) {
        YearMonth target = (period == null || period.isBlank()) ? YearMonth.now() : YearMonth.parse(period);
        int processed = orchestrator.runMonthlySettlement(target);
        return new SettlementRunResponse(target.toString(), processed);
    }

    @PostMapping("/api/admin/tiering/grace-sweep")
    public GraceSweepResponse graceSweep() {
        return new GraceSweepResponse(orchestrator.sweepExpiredGrace());
    }
}

package com.firstclub.membership.tiering;

import com.firstclub.membership.catalog.domain.Tier;
import com.firstclub.membership.common.Money;
import com.firstclub.membership.tiering.settlement.DifferencePricingPolicy;
import com.firstclub.membership.tiering.settlement.SettlementDecision;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DifferencePricingPolicyTest {

    private final DifferencePricingPolicy policy = new DifferencePricingPolicy();

    private Tier tier(String name, int rank, long monthlyFee) {
        Tier t = new Tier();
        t.setName(name);
        t.setRank(rank);
        t.setMonthlyFee(Money.ofRupees(monthlyFee));
        t.setJoiningFee(Money.ZERO);
        return t;
    }

    @Test
    void waivesAndKeepsWhenFreeEligibleEqualsHeld() {
        Tier gold = tier("Gold", 1, 199);

        SettlementDecision decision = policy.settle(gold, gold);

        assertThat(decision.feeRequired()).isFalse();
        assertThat(decision.feeOwed()).isEqualTo(Money.ZERO);
        assertThat(decision.tierIfUnpaid()).isEqualTo(gold);
    }

    @Test
    void autoPromotesWhenFreeEligibleAboveHeld() {
        Tier gold = tier("Gold", 1, 199);
        Tier platinum = tier("Platinum", 2, 499);

        SettlementDecision decision = policy.settle(gold, platinum);

        assertThat(decision.feeRequired()).isFalse();
        assertThat(decision.tierIfUnpaid()).isEqualTo(platinum); // promoted, free
    }

    @Test
    void chargesDifferenceWhenFreeEligibleBelowHeld() {
        Tier gold = tier("Gold", 1, 199);
        Tier platinum = tier("Platinum", 2, 499);

        SettlementDecision decision = policy.settle(platinum, gold);

        assertThat(decision.feeRequired()).isTrue();
        assertThat(decision.feeOwed()).isEqualTo(Money.ofRupees(300)); // 499 - 199
        assertThat(decision.tierIfPaid()).isEqualTo(platinum);
        assertThat(decision.tierIfUnpaid()).isEqualTo(gold);
    }

    @Test
    void chargesFullHeldFeeWhenOnlyBaseEarnedFree() {
        Tier silver = tier("Silver", 0, 0);
        Tier platinum = tier("Platinum", 2, 499);

        SettlementDecision decision = policy.settle(platinum, silver);

        assertThat(decision.feeOwed()).isEqualTo(Money.ofRupees(499)); // 499 - 0
        assertThat(decision.tierIfUnpaid()).isEqualTo(silver);
    }
}
